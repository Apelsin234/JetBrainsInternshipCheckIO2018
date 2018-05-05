import retrofit2.Response;
import retrofit2.Retrofit;

import retrofit2.converter.gson.GsonConverterFactory;


import java.io.IOException;
import java.util.List;

public class Main {
    private static StepikApi stepikApi;

    public static void main(String[] args) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://stepic.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        stepikApi = retrofit.create(StepikApi.class);
        int i = 1;

        Response<JsonModel> response = null;
        try {
            response = getApi().getPage(i).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response != null && response.isSuccessful()) {
            JsonModel json = response.body();
            List<JsonModel.Course> courses = json.getCourses();
            for (JsonModel.Course it: courses ) {
                System.out.println(it.getTitle() + " " + it.getLearnersCount());

            }
        }


    }

    private static StepikApi getApi() {
        return stepikApi;
    }
}
