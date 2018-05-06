import javafx.util.Pair;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Phaser;

public class Main {

    private static final int EXPECTED_COURSES_COUNT = 3000;
    private static final String STEPIK_URL = "https://stepic.org";
    private static final String OUTPUT_TITLE = "№) НазваниеНазвание курса : Количество слушателей";
    private static final String TIME_EX = "Время выполнения";


    private int n;
    private StepikApi stepikApi;
    private Comparator<Pair<String, Integer>> cmpPair = Comparator.comparingInt(Pair::getValue);

    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();
        if (args == null || args.length != 1 || args[0] == null || !args[0].matches("\\d+")) {
            throw new RuntimeException("Invalid arguments. Expected {N - count favorite courses that need print}");
        }
        Main main = new Main();

        if ((main.n = Integer.parseInt(args[0])) == 0) {
            return;
        }

        main.initRetrofit();
        PriorityQueue<Pair<String, Integer>> courses = main.getListCourses();
        main.printFavoriteCourses(courses);
        long timeSpent = System.currentTimeMillis() - startTime;
        System.out.println(TIME_EX + timeSpent);
    }

    private void printFavoriteCourses(PriorityQueue<Pair<String, Integer>> courses) {
        int ind = courses.size();
        System.out.println(OUTPUT_TITLE);
        while (!courses.isEmpty()) {
            Pair<String, Integer> pair = courses.poll();
            System.out.println(ind-- + ") " + pair.getKey() + " : " + pair.getValue());
        }
    }

    private boolean putCoursesToPriorityQ(final Response<JsonModel> response, final PriorityQueue<Pair<String, Integer>> pqCourses) {
        if (response != null && response.isSuccessful()) {
            JsonModel json = response.body();
            List<JsonModel.Course> courses = json.getCourses();

            for (JsonModel.Course it : courses) {
                synchronized (pqCourses) {
                    if (pqCourses.size() < n || pqCourses.peek().getValue().compareTo(it.getLearnersCount()) < 0) {
                        pqCourses.add(new Pair<>(it.getTitle(), it.getLearnersCount()));
                        if (pqCourses.size() > n) {
                            pqCourses.poll();
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    private void addTasks(int from, int to, final PriorityQueue<Pair<String, Integer>> pqCourses, final Phaser phaser) {
        for (int i = from; i < to; i++) {
            stepikApi.getPage(i).enqueue(new CallBackJson(pqCourses, phaser));
        }
    }

    private PriorityQueue<Pair<String, Integer>> getListCourses() {
        int numPage = 1;
        PriorityQueue<Pair<String, Integer>> pqCourses = new PriorityQueue<>(Math.min(n + 1, EXPECTED_COURSES_COUNT), cmpPair);
        Phaser phaser = new Phaser(1);
        int step = 50;
        Response<JsonModel> response;
        while (true) {
            try {
                int lastPage = numPage + step - 1;
                response = stepikApi.getPage(lastPage).execute();
                if (putCoursesToPriorityQ(response, pqCourses)) {
                    phaser.bulkRegister(step - 1);
                    addTasks(numPage, lastPage, pqCourses, phaser);
                    numPage += step;
                } else {
                    do {
                        response = stepikApi.getPage(numPage++).execute();
                    } while (putCoursesToPriorityQ(response, pqCourses));
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException("An error occurred during networking", e);
            }

        }
        phaser.arriveAndAwaitAdvance();
        return pqCourses;

    }

    private void initRetrofit() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(STEPIK_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        stepikApi = retrofit.create(StepikApi.class);
    }

    private class CallBackJson implements Callback<JsonModel> {

        private final PriorityQueue<Pair<String, Integer>> pqCourses;
        private final Phaser phaser;

        CallBackJson(PriorityQueue<Pair<String, Integer>> pqCourses, Phaser phaser) {
            this.pqCourses = pqCourses;
            this.phaser = phaser;

        }


        @Override
        public void onResponse(Call<JsonModel> call, Response<JsonModel> response) {
            putCoursesToPriorityQ(response, pqCourses);
            phaser.arrive();
        }

        @Override
        public void onFailure(Call<JsonModel> call, Throwable t) {
            throw new RuntimeException("An error occurred during networking", t);
        }
    }


}
