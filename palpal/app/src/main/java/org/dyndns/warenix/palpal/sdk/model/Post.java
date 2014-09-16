package org.dyndns.warenix.palpal.sdk.model;

import android.net.Uri;

/**
 * Created by warenix on 8/23/14.
 */
public class Post extends BaseResponse {

    private String id;

    private String message;

    private String picture;

    private String name;

    private String description;

    private String type;

    private String created_time;

    private String updated_time;

    private Property[] properties;

    private String status_type;

    /**
     * larger picture image
     */
    private String pictureLarge;

//    public static Post factoryFromCursor(Cursor cursor) {
//        Post obj = new Post();
//        obj.name = cursor.getString(
//                cursor.getColumnIndex(CreativeNewsDatabaseHelper.TablePostList.COLUMN_NAME));
//        obj.description = cursor.getString(
//                cursor.getColumnIndex(CreativeNewsDatabaseHelper.TablePostList.COLUMN_DESCRIPTION));
//        obj.id = cursor.getString(
//                cursor.getColumnIndex(CreativeNewsDatabaseHelper.TablePostList.COLUMN_ID));
//        obj.message = cursor.getString(
//                cursor.getColumnIndex(CreativeNewsDatabaseHelper.TablePostList.COLUMN_MESSAGE));
//        obj.picture = cursor.getString(
//                cursor.getColumnIndex(CreativeNewsDatabaseHelper.TablePostList.COLUMN_PICTURE));
//        obj.type = cursor.getString(
//                cursor.getColumnIndex(CreativeNewsDatabaseHelper.TablePostList.COLUMN_TYPE));
//        obj.created_time = cursor.getString(
//                cursor.getColumnIndex(
//                        CreativeNewsDatabaseHelper.TablePostList.COLUMN_CREATED_TIME));
//        obj.updated_time = cursor.getString(
//                cursor.getColumnIndex(
//                        CreativeNewsDatabaseHelper.TablePostList.COLUMN_UPDATED_TIME));
//        obj.status_type = cursor.getString(
//                cursor.getColumnIndex(
//                        CreativeNewsDatabaseHelper.TablePostList.COLUMN_STATUS_TYPE));
//        return obj;
//    }

    public String getStatusType() {
        return status_type;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getPicture() {
        return getPicture(false);
    }

    public String getPicture(boolean large) {
        if (large) {
            if (pictureLarge == null) {
                String filename = Uri.parse(picture).getLastPathSegment();
                pictureLarge = String
                    .format(
                        "https://fbcdn-sphotos-c-a.akamaihd.net/hphotos-ak-xfp1/t1.0-9/q71/s720x720/%s",
                        filename);

            }
            return pictureLarge;

        }
        return picture;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getCreatedTime() {
        return created_time;
    }

    public String getUpdatedTime() {
        return updated_time;
    }

    public Property[] getPropertyList() {
        return properties;
    }

    public static class Property {

        private String name;

        private String text;

        public String getName() {
            return name;
        }

        public String getText() {
            return text;
        }
    }
}
