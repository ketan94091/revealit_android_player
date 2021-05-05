package com.Revealit.ModelClasses;

import java.util.List;

public class DotsLocationsModel {

    private List<Datum> data = null;

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }


    public class Datum {

        private Integer frameId;
        private String itemId;
        private Float x_Axis;
        private Float y_Axis;
        private Float xRight;
        private Float yRight;
        private Float x_left;
        private Float y_left;
        private Integer dotAnimate;
        private Integer dotFilled;
        private Integer dotSize;
        private String item_dot_color;
        private String item_label_color;
        private Integer itemLabelSize;
        private Integer labelSize;
        private Integer transition;
        private String item_name;
        private String vendor;
        private String vendorUrl;
        private String armodel;
        private String armodelUrl;
        private Integer armodelId;
        private String armodelSponsor;
        private String armodelSponsorImgUrl;
        private ItemWiki itemWiki;
        private String blueDotColor;
        private List<BlueDotMetum> blueDotMeta = null;
        private Integer order;
        private String arDotColor;
        private Float leftRatio;
        private Float topRatio;
        private Float rightRatio;
        private Float bottomRatio;
        private Float xRatio;
        private Float yRatio;

        public Integer getFrameId() {
            return frameId;
        }

        public void setFrameId(Integer frameId) {
            this.frameId = frameId;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public Float getxAxis() {
            return x_Axis;
        }

        public void setxAxis(Float xAxis) {
            this.x_Axis = xAxis;
        }

        public Float getyAxis() {
            return y_Axis;
        }

        public void setyAxis(Float yAxis) {
            this.y_Axis = yAxis;
        }

        public Float getxRight() {
            return xRight;
        }

        public void setxRight(Float xRight) {
            this.xRight = xRight;
        }

        public Float getyRight() {
            return yRight;
        }

        public void setyRight(Float yRight) {
            this.yRight = yRight;
        }

        public Float getxLeft() {
            return x_left;
        }

        public void setxLeft(Float xLeft) {
            this.x_left = xLeft;
        }

        public Float getyLeft() {
            return y_left;
        }

        public void setyLeft(Float yLeft) {
            this.y_left = yLeft;
        }

        public Integer getDotAnimate() {
            return dotAnimate;
        }

        public void setDotAnimate(Integer dotAnimate) {
            this.dotAnimate = dotAnimate;
        }

        public Integer getDotFilled() {
            return dotFilled;
        }

        public void setDotFilled(Integer dotFilled) {
            this.dotFilled = dotFilled;
        }

        public Integer getDotSize() {
            return dotSize;
        }

        public void setDotSize(Integer dotSize) {
            this.dotSize = dotSize;
        }

        public String getItemDotColor() {
            return item_dot_color;
        }

        public void setItemDotColor(String item_dot_color) {
            this.item_dot_color = item_dot_color;
        }

        public String getItemLabelColor() {
            return item_label_color;
        }

        public void setItemLabelColor(String item_label_color) {
            this.item_label_color = item_label_color;
        }

        public Integer getItemLabelSize() {
            return itemLabelSize;
        }

        public void setItemLabelSize(Integer itemLabelSize) {
            this.itemLabelSize = itemLabelSize;
        }

        public Integer getLabelSize() {
            return labelSize;
        }

        public void setLabelSize(Integer labelSize) {
            this.labelSize = labelSize;
        }

        public Integer getTransition() {
            return transition;
        }

        public void setTransition(Integer transition) {
            this.transition = transition;
        }

        public String getItemName() {
            return item_name;
        }

        public void setItemName(String item_name) {
            this.item_name = item_name;
        }

        public String getVendor() {
            return vendor;
        }

        public void setVendor(String vendor) {
            this.vendor = vendor;
        }

        public String getVendorUrl() {
            return vendorUrl;
        }

        public void setVendorUrl(String vendorUrl) {
            this.vendorUrl = vendorUrl;
        }

        public String getArmodel() {
            return armodel;
        }

        public void setArmodel(String armodel) {
            this.armodel = armodel;
        }

        public String getArmodelUrl() {
            return armodelUrl;
        }

        public void setArmodelUrl(String armodelUrl) {
            this.armodelUrl = armodelUrl;
        }

        public Integer getArmodelId() {
            return armodelId;
        }

        public void setArmodelId(Integer armodelId) {
            this.armodelId = armodelId;
        }

        public String getArmodelSponsor() {
            return armodelSponsor;
        }

        public void setArmodelSponsor(String armodelSponsor) {
            this.armodelSponsor = armodelSponsor;
        }

        public String getArmodelSponsorImgUrl() {
            return armodelSponsorImgUrl;
        }

        public void setArmodelSponsorImgUrl(String armodelSponsorImgUrl) {
            this.armodelSponsorImgUrl = armodelSponsorImgUrl;
        }

        public ItemWiki getItemWiki() {
            return itemWiki;
        }

        public void setItemWiki(ItemWiki itemWiki) {
            this.itemWiki = itemWiki;
        }

        public String getBlueDotColor() {
            return blueDotColor;
        }

        public void setBlueDotColor(String blueDotColor) {
            this.blueDotColor = blueDotColor;
        }

        public List<BlueDotMetum> getBlueDotMeta() {
            return blueDotMeta;
        }

        public void setBlueDotMeta(List<BlueDotMetum> blueDotMeta) {
            this.blueDotMeta = blueDotMeta;
        }

        public Integer getOrder() {
            return order;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }

        public String getArDotColor() {
            return arDotColor;
        }

        public void setArDotColor(String arDotColor) {
            this.arDotColor = arDotColor;
        }

        public Float getLeftRatio() {
            return leftRatio;
        }

        public void setLeftRatio(Float leftRatio) {
            this.leftRatio = leftRatio;
        }

        public Float getTopRatio() {
            return topRatio;
        }

        public void setTopRatio(Float topRatio) {
            this.topRatio = topRatio;
        }

        public Float getRightRatio() {
            return rightRatio;
        }

        public void setRightRatio(Float rightRatio) {
            this.rightRatio = rightRatio;
        }

        public Float getBottomRatio() {
            return bottomRatio;
        }

        public void setBottomRatio(Float bottomRatio) {
            this.bottomRatio = bottomRatio;
        }

        public Float getxRatio() {
            return xRatio;
        }

        public void setxRatio(Float xRatio) {
            this.xRatio = xRatio;
        }

        public Float getyRatio() {
            return yRatio;
        }

        public void setyRatio(Float yRatio) {
            this.yRatio = yRatio;
        }

    }

    public class BlueDotMetum {

        private Integer id;
        private String title;
        private String url;
        private List<Item> items = null;
        private Integer typeId;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }

        public Integer getTypeId() {
            return typeId;
        }

        public void setTypeId(Integer typeId) {
            this.typeId = typeId;
        }

    }

    public class Item {

        private Integer productId;
        private String productName;

        public Integer getProductId() {
            return productId;
        }

        public void setProductId(Integer productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

    }

    public class ItemWiki {

        private Integer id;
        private String sponsorName;
        private String sponsorImageUrl;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getSponsorName() {
            return sponsorName;
        }

        public void setSponsorName(String sponsorName) {
            this.sponsorName = sponsorName;
        }

        public String getSponsorImageUrl() {
            return sponsorImageUrl;
        }

        public void setSponsorImageUrl(String sponsorImageUrl) {
            this.sponsorImageUrl = sponsorImageUrl;
        }

    }
}
