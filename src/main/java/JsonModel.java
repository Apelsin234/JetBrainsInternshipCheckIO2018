import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonModel {

    public class Course {
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("learners_count")
        @Expose
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
        @SerializedName("has_next")
        @Expose
        private boolean has_next;
        @SerializedName("has_previous")
        @Expose
        private boolean has_previous;
        @SerializedName("page")
        @Expose
        private int page;

        public boolean hasNext() {
            return has_next;
        }

        public void setHasNext(boolean has_next) {
            this.has_next = has_next;
        }

        public boolean hasPrevious() {
            return has_previous;
        }

        public void setHasPrevious(boolean has_previous) {
            this.has_previous = has_previous;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }
    }

    @SerializedName("courses")
    @Expose
    private List<Course> courses;
    @SerializedName("meta")
    @Expose
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