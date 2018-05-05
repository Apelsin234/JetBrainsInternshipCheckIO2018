import javafx.util.Pair;
import retrofit2.Response;
import retrofit2.Retrofit;

import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import static java.util.stream.Collectors.toList;

public class Main {

    private final static int EXPECTED_COURSES_COUNT = 3000;
    private final static String STEPIK_URL = "https://stepic.org";
    private static int n;

    private static StepikApi stepikApi;
    private static Comparator<Pair<String,Integer>> cmpPair = Comparator.comparingInt(Pair::getValue);

    public static void main(String[] args) {
        n = Integer.parseInt(args[0]);
        initRetrofit();
        PriorityQueue<Pair<String, Integer>> courses = getListCourses();
        printFavoriteCourses(courses);
    }

    private static void printFavoriteCourses(PriorityQueue<Pair<String, Integer>> courses) {
        int ind = n;
        while(!courses.isEmpty()) {
            Pair<String, Integer> pair = courses.poll();
            System.out.println(n-- + ") " + pair.getKey() + " : " + pair.getValue());
        }
    }

    private static PriorityQueue<Pair<String, Integer>> getListCourses() {
        int numPage = 1;
        PriorityQueue<Pair<String, Integer>> pq = new PriorityQueue<>(Math.min(n + 1, EXPECTED_COURSES_COUNT), cmpPair);

        while (true) {
            Response<JsonModel> response;
            try {
                response = stepikApi.getPage(numPage).execute();
            } catch (IOException e) {
                throw new RuntimeException("An error was occurred when get page.", e);
            }
            if (response != null && response.isSuccessful()) {
                JsonModel json = response.body();
                List<JsonModel.Course> courses = json.getCourses();

                for(JsonModel.Course it : courses) {
                    if(pq.size() < n || pq.peek().getValue().compareTo(it.getLearnersCount()) < 0) {
                        pq.add(new Pair<>(it.getTitle(), it.getLearnersCount()));
                        if(pq.size() > n){
                            pq.poll();
                        }
                    }
                }

                numPage++;
                if (!json.getMeta().hasNext()) {
                    break;
                }
            } else {
                throw new RuntimeException("An error occurred during networking ");
            }

        }
        return pq;

    }

    private static void initRetrofit() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(STEPIK_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        stepikApi = retrofit.create(StepikApi.class);
    }


}
