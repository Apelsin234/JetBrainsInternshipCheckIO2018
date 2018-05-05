import javafx.util.Pair;
import retrofit2.Response;
import retrofit2.Retrofit;

import retrofit2.converter.gson.GsonConverterFactory;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class Main {

    private final static int EXPECTED_COUNT_OF_COURSES = 3000;

    private static StepikApi stepikApi;

    public static void main(String[] args) {
        int n = 10;
        initRetrofit();
        List<Pair<String, Integer>> courses = getListCourses();
        printNFavoriteCourses(courses, n);
    }

    private static void printNFavoriteCourses(List<Pair<String, Integer>> courses, int n) {
        courses.sort((a, b) -> 0 - Integer.compare(a.getValue(), b.getValue()));
        for(int i = 0; i < Math.min(n, courses.size()); i++) {
            System.out.println(i + 1 + ") " + courses.get(i).getKey() + " : " + courses.get(i).getValue());
        }
    }

    private static List<Pair<String, Integer>> getListCourses() {
        int numPage = 1;
//        int count = 1;
        List<Pair<String, Integer>> coursesAns = new ArrayList<>(EXPECTED_COUNT_OF_COURSES);
        while (true) {
            Response<JsonModel> response;
            try {
                response = stepikApi.getPage(numPage).execute();
            } catch (IOException e) {
                throw new RuntimeException("An error was occurred when get page.");
            }
            if (response != null && response.isSuccessful()) {
                JsonModel json = response.body();
                List<JsonModel.Course> courses = json.getCourses();

                coursesAns.addAll(courses.stream()
                        .map(course -> new Pair<>(course.getTitle(), course.getLearnersCount()))
                        .collect(toList()));
//                for (JsonModel.Course it : courses) {
//                    System.out.println(count++ + ") " + it.getTitle() + " : " + it.getLearnersCount());
//                }
                numPage++;
                if (!json.getMeta().hasNext()) {
                    break;
                }
            } else {
                throw new RuntimeException("An error occurred during networking ");
            }

        }
        return coursesAns;

    }

    private static void initRetrofit() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://stepic.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        stepikApi = retrofit.create(StepikApi.class);
    }


}
