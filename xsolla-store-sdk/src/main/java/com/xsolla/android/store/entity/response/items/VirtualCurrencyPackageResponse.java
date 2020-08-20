package com.xsolla.android.store.entity.response.items;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.xsolla.android.store.entity.response.common.Content;
import com.xsolla.android.store.entity.response.common.Group;
import com.xsolla.android.store.entity.response.common.Price;
import com.xsolla.android.store.entity.response.common.VirtualPrice;

import java.util.List;

public class VirtualCurrencyPackageResponse {
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public class Item implements Parcelable {
        private String sku;
        private String name;
        private List<Group> groups;
        private List<Object> attribites;
        private String type;

        @SerializedName("bundle_type")
        private String bundleType;
        private String description;

        @SerializedName("image_url")
        private String imageUrl;

        @SerializedName("is_free")
        private boolean isFree;
        private Price price;

        @SerializedName("virtual_prices")
        private List<VirtualPrice> virtualPrices;

        private List<Content> content;

        protected Item(Parcel in) {
            sku = in.readString();
            name = in.readString();
            type = in.readString();
            bundleType = in.readString();
            description = in.readString();
            imageUrl = in.readString();
            isFree = in.readByte() != 0;
            price = in.readParcelable(Price.class.getClassLoader());
            virtualPrices = in.createTypedArrayList(VirtualPrice.CREATOR);
        }

        public final Creator<Item> CREATOR = new Creator<Item>() {
            @Override
            public Item createFromParcel(Parcel in) {
                return new Item(in);
            }

            @Override
            public Item[] newArray(int size) {
                return new Item[size];
            }
        };

        public String getSku() {
            return sku;
        }

        public String getName() {
            return name;
        }

        public List<Group> getGroups() {
            return groups;
        }

        public List<Object> getAttribites() {
            return attribites;
        }

        public String getType() {
            return type;
        }

        public String getBundleType() {
            return bundleType;
        }

        public String getDescription() {
            return description;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public boolean isFree() {
            return isFree;
        }

        public Price getPrice() {
            return price;
        }

        public List<VirtualPrice> getVirtualPrices() {
            return virtualPrices;
        }

        public List<Content> getContent() {
            return content;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(sku);
            dest.writeString(name);
            dest.writeString(type);
            dest.writeString(bundleType);
            dest.writeString(description);
            dest.writeString(imageUrl);
            dest.writeByte((byte) (isFree ? 1 : 0));
            dest.writeParcelable(price, flags);
            dest.writeTypedList(virtualPrices);
        }
    }
}
