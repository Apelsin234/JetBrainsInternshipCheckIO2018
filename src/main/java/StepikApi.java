import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface StepikApi {

    @GET("/api/courses")
    Call<JsonModel> getPage(@Query("page") int page);
}
