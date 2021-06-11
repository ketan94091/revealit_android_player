package com.Revealit.ModelClasses;

import java.util.List;

public class GetRecipesDetails {

    private List<Data> data = null;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }


    public class Data{

        private Integer id;
        private String name;
        private String video_id;
        private String recipe_by;
        private String description;
        private String url;
        private String flat_ingredients;
        private String flat_directions;
        private String shopping_url;
        private String recipe_image;
        private Chef chef;
        private String armodel;
        private String armodel_url;
        private Integer armodel_id;
        private String armodel_name;
        private String armodel_sponsor;
        private String armodel_sponsor_img_url;
        private String recipe_advert_img_url;
        private List<Object> ingredients = null;
        private List<Object> directions = null;


        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVideo_id() {
            return video_id;
        }

        public void setVideo_id(String video_id) {
            this.video_id = video_id;
        }

        public String getRecipe_by() {
            return recipe_by;
        }

        public void setRecipe_by(String recipe_by) {
            this.recipe_by = recipe_by;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getFlat_ingredients() {
            return flat_ingredients;
        }

        public void setFlat_ingredients(String flat_ingredients) {
            this.flat_ingredients = flat_ingredients;
        }

        public String getFlat_directions() {
            return flat_directions;
        }

        public void setFlat_directions(String flat_directions) {
            this.flat_directions = flat_directions;
        }

        public String getShopping_url() {
            return shopping_url;
        }

        public void setShopping_url(String shopping_url) {
            this.shopping_url = shopping_url;
        }

        public String getRecipe_image() {
            return recipe_image;
        }

        public void setRecipe_image(String recipe_image) {
            this.recipe_image = recipe_image;
        }

        public Chef getChef() {
            return chef;
        }

        public void setChef(Chef chef) {
            this.chef = chef;
        }

        public String getArmodel() {
            return armodel;
        }

        public void setArmodel(String armodel) {
            this.armodel = armodel;
        }

        public String getArmodel_url() {
            return armodel_url;
        }

        public void setArmodel_url(String armodel_url) {
            this.armodel_url = armodel_url;
        }

        public Integer getArmodel_id() {
            return armodel_id;
        }

        public void setArmodel_id(Integer armodel_id) {
            this.armodel_id = armodel_id;
        }

        public String getArmodel_name() {
            return armodel_name;
        }

        public void setArmodel_name(String armodel_name) {
            this.armodel_name = armodel_name;
        }

        public String getArmodel_sponsor() {
            return armodel_sponsor;
        }

        public void setArmodel_sponsor(String armodel_sponsor) {
            this.armodel_sponsor = armodel_sponsor;
        }

        public String getArmodel_sponsor_img_url() {
            return armodel_sponsor_img_url;
        }

        public void setArmodel_sponsor_img_url(String armodel_sponsor_img_url) {
            this.armodel_sponsor_img_url = armodel_sponsor_img_url;
        }

        public String getRecipe_advert_img_url() {
            return recipe_advert_img_url;
        }

        public void setRecipe_advert_img_url(String recipe_advert_img_url) {
            this.recipe_advert_img_url = recipe_advert_img_url;
        }

        public List<Object> getIngredients() {
            return ingredients;
        }

        public void setIngredients(List<Object> ingredients) {
            this.ingredients = ingredients;
        }

        public List<Object> getDirections() {
            return directions;
        }

        public void setDirections(List<Object> directions) {
            this.directions = directions;
        }
    }

    public class Chef{

        private String name;
        private String img_url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImg_url() {
            return img_url;
        }

        public void setImg_url(String img_url) {
            this.img_url = img_url;
        }
    }


}
