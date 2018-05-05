import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonModel {

    public class Course {

        @Expose
        @SerializedName("title")
        private String title;

        @Expose
        @SerializedName("learners_count")
        private int learners_count;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getLearnersCount() {
            return learners_count;
        }

        public void setLearnersCount(int learners_count) {
            this.learners_count = learners_count;
        }
    }

    public class Meta {

        @Expose
        @SerializedName("has_next")
        private boolean has_next;

        public boolean hasNext() {
            return has_next;
        }

        public void setHasNext(boolean has_next) {
            this.has_next = has_next;
        }

    }


    @Expose
    @SerializedName("courses")
    private List<Course> courses;

    @Expose
    @SerializedName("meta")
    private Meta meta;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }


}