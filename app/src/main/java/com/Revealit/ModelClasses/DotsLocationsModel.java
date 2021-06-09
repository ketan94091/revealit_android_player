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
        private String item_id;
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
        private String vendor_url;
        private String armodel;
        private String armodel_url;
        private Integer armodelId;
        private String armodel_sponsor;
        private String armodelSponsorImgUrl;
        private ItemWiki item_wiki;
        private String blue_dot_color;
        private List<BlueDotMetum> blue_dot_meta = null;
        private Integer order;
        private String ar_dot_color;
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
            return item_id;
        }

        public void setItemId(String item_id) {
            this.item_id = item_id;
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
            return vendor_url;
        }

        public void setVendorUrl(String vendor_url) {
            this.vendor_url = vendor_url;
        }

        public String getArmodel() {
            return armodel;
        }

        public void setArmodel(String armodel) {
            this.armodel = armodel;
        }

        public String getArmodelUrl() {
            return armodel_url;
        }

        public void setArmodelUrl(String armodel_url) {
            this.armodel_url = armodel_url;
        }

        public Integer getArmodelId() {
            return armodelId;
        }

        public void setArmodelId(Integer armodelId) {
            this.armodelId = armodelId;
        }

        public String getArmodelSponsor() {
            return armodel_sponsor;
        }

        public void setArmodelSponsor(String armodel_sponsor) {
            this.armodel_sponsor = armodel_sponsor;
        }

        public String getArmodelSponsorImgUrl() {
            return armodelSponsorImgUrl;
        }

        public void setArmodelSponsorImgUrl(String armodelSponsorImgUrl) {
            this.armodelSponsorImgUrl = armodelSponsorImgUrl;
        }

        public ItemWiki getItemWiki() {
            return item_wiki;
        }

        public void setItemWiki(ItemWiki item_wiki) {
            this.item_wiki = item_wiki;
        }

        public String getBlueDotColor() {
            return blue_dot_color;
        }

        public void setBlueDotColor(String blue_dot_color) {
            this.blue_dot_color = blue_dot_color;
        }

        public List<BlueDotMetum> getBlueDotMeta() {
            return blue_dot_meta;
        }

        public void setBlueDotMeta(List<BlueDotMetum> blue_dot_meta) {
            this.blue_dot_meta = blue_dot_meta;
        }

        public Integer getOrder() {
            return order;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }

        public String getArDotColor() {
            return ar_dot_color;
        }

        public void setArDotColor(String ar_dot_color) {
            this.ar_dot_color = ar_dot_color;
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
        private Integer type_id;

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
            return type_id;
        }

        public void setTypeId(Integer type_id) {
            this.type_id = type_id;
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
        private String sponsor_name;
        private String sponsor_image_url;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getSponsorName() {
            return sponsor_name;
        }

        public void setSponsorName(String sponsor_name) {
            this.sponsor_name = sponsor_name;
        }

        public String getSponsorImageUrl() {
            return sponsor_image_url;
        }

        public void setSponsorImageUrl(String sponsor_image_url) {
            this.sponsor_image_url = sponsor_image_url;
        }

    }
}
