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

    private final static int EXPECTED_COURSES_COUNT = 3000;
    private final static String STEPIK_URL = "https://stepic.org";

    private static int n;
    private static StepikApi stepikApi;
    private static Comparator<Pair<String, Integer>> cmpPair = Comparator.comparingInt(Pair::getValue);

    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();
        if (args == null || args.length != 1 || args[0] == null || !args[0].matches("\\d+")) {
            throw new RuntimeException("Invalid arguments. Expected {N - count favorite courses that need print}");
        }
        n = Integer.parseInt(args[0]);

        initRetrofit();
        PriorityQueue<Pair<String, Integer>> courses = getListCourses();
        printFavoriteCourses(courses);
        long timeSpent = System.currentTimeMillis() - startTime;
        System.out.println("Время выполнения - " + timeSpent);
    }

    private static void printFavoriteCourses(PriorityQueue<Pair<String, Integer>> courses) {
        int ind = courses.size();

        while (!courses.isEmpty()) {
            Pair<String, Integer> pair = courses.poll();
            System.out.println(ind-- + ") " + pair.getKey() + " : " + pair.getValue());
        }
    }


    private static PriorityQueue<Pair<String, Integer>> getListCourses() {
        int numPage;
        PriorityQueue<Pair<String, Integer>> pqCourses = new PriorityQueue<>(Math.min(n + 1, EXPECTED_COURSES_COUNT), cmpPair);
        Phaser phaser = new Phaser(269);
        for(numPage = 1; numPage < 269; numPage++) {

                stepikApi.getPage(numPage).enqueue(new Callback<JsonModel>() {
                    @Override
                    public void onResponse(Call<JsonModel> call, Response<JsonModel> response) {
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
                        } else {
                            throw new RuntimeException("Response has null value or we call invalid page");
                        }
                        phaser.arrive();
                    }

                    @Override
                    public void onFailure(Call<JsonModel> call, Throwable t) {
                        throw new RuntimeException("An error occurred during networking", t);
                    }
                });

        }
        phaser.arriveAndAwaitAdvance();
        return pqCourses;

    }

    private static void initRetrofit() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(STEPIK_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        stepikApi = retrofit.create(StepikApi.class);
    }


}
