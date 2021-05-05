package com.Revealit.ModelClasses;

import java.util.List;

public class CategoryNamesListModel {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }


    public static class DataBean {

        private String categoryName;
        private String slugName;


        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getSlugName() {
            return slugName;
        }

        public void setSlugName(String slugName) {
            this.slugName = slugName;
        }
    }
}
