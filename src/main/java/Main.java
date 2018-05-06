import javafx.util.Pair;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Main {

    private final static int EXPECTED_COURSES_COUNT = 3000;
    private final static String STEPIK_URL = "https://stepic.org";

    private static int n;
    private static StepikApi stepikApi;
    private static Comparator<Pair<String, Integer>> cmpPair = Comparator.comparingInt(Pair::getValue);

    public static void main(String[] args) {
        if (args == null || args.length != 1 || args[0] == null ||
                !args[0].matches("\\d+") || (n = Integer.parseInt(args[0])) == 0) {
            throw new RuntimeException("Invalid arguments. Expected {N - count favorite courses that need print}");
        }

        initRetrofit();
        PriorityQueue<Pair<String, Integer>> courses = getListCourses();
        printFavoriteCourses(courses);
    }

    private static void printFavoriteCourses(PriorityQueue<Pair<String, Integer>> courses) {
        int ind = courses.size();
        System.out.println("№) Название курса : Количество слушателей");
        while (!courses.isEmpty()) {
            Pair<String, Integer> pair = courses.poll();
            System.out.println(ind-- + ") " + pair.getKey() + " : " + pair.getValue());
        }
    }

    private static PriorityQueue<Pair<String, Integer>> getListCourses() {
        int numPage = 1;
        PriorityQueue<Pair<String, Integer>> pqCourses = new PriorityQueue<>(Math.min(n + 1, EXPECTED_COURSES_COUNT), cmpPair);

        while (true) {
            Response<JsonModel> response;
            try {
                response = stepikApi.getPage(numPage++).execute();

                if (response != null && response.isSuccessful()) {
                    JsonModel json = response.body();
                    List<JsonModel.Course> courses = json.getCourses();

                    for (JsonModel.Course it : courses) {
                        if (pqCourses.size() < n || pqCourses.peek().getValue().compareTo(it.getLearnersCount()) < 0) {
                            pqCourses.add(new Pair<>(it.getTitle(), it.getLearnersCount()));
                            if (pqCourses.size() > n) {
                                pqCourses.poll();
                            }
                        }
                    }

                    if (!json.getMeta().hasNext()) {
                        break;
                    }
                } else {
                    throw new RuntimeException("Response has null value or we call invalid page");
                }
            } catch (IOException e) {
                throw new RuntimeException("An error occurred during networking", e);
            }

        }
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
