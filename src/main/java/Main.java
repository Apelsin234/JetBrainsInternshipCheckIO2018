import retrofit2.Response;
import retrofit2.Retrofit;

import retrofit2.converter.gson.GsonConverterFactory;


import java.io.IOException;
import java.util.List;

public class Main {


    public static void main(String[] args) {
        StepikApi stepikApi;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://stepic.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        stepikApi = retrofit.create(StepikApi.class);
        int i = 1;
        int num = 1;
        while (true) {
            Response<JsonModel> response = null;
            try {
                response = stepikApi.getPage(i).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null && response.isSuccessful()) {
                JsonModel json = response.body();
                List<JsonModel.Course> courses = json.getCourses();
                for (JsonModel.Course it : courses) {
                    System.out.println(num++ + ") " + it.getTitle() + " : " + it.getLearnersCount());

                }
                i++;
                if(!json.getMeta().hasNext()) {
                    break;
                }
            } else {
                throw new RuntimeException("An error was occurred when get response. ");
            }

        }


    }


}
