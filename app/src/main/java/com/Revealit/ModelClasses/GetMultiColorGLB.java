package com.Revealit.ModelClasses;

import java.util.List;

public class GetMultiColorGLB {

    private List<Data> data = null;
    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }


        public class Data {

            private int id;
            private String glb_filename;
            private String glb_color_hex;
            private String glb_color_name;


            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getGlb_filename() {
                return glb_filename;
            }

            public void setGlb_filename(String glb_filename) {
                this.glb_filename = glb_filename;
            }

            public String getGlb_color_hex() {
                return glb_color_hex;
            }

            public void setGlb_color_hex(String glb_color_hex) {
                this.glb_color_hex = glb_color_hex;
            }

            public String getGlb_color_name() {
                return glb_color_name;
            }

            public void setGlb_color_name(String glb_color_name) {
                this.glb_color_name = glb_color_name;
            }
        }



}
