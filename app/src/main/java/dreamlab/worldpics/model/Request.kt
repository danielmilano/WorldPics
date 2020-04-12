package dreamlab.worldpics.model

import android.os.Parcel
import android.os.Parcelable

class Request(var q: String? = null,
              var lang: String? = null,
              var image_type: String? = null,
              var orientation: String? = null,
              var category: String? = null,
              var min_width: Int? = null,
              var min_height: Int? = null,
              var colors: String? = null,
              var editors_choice: Boolean? = false,
              var order: String? = null,
              var page: Int? = 0,
              var per_page: Int? = 20) : Parcelable {

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(q)
        dest.writeValue(lang)
        dest.writeValue(image_type)
        dest.writeValue(orientation)
        dest.writeValue(category)
        dest.writeValue(min_width)
        dest.writeValue(min_height)
        dest.writeValue(colors)
        dest.writeValue(editors_choice)
        dest.writeValue(order)
        dest.writeValue(page)
        dest.writeValue(per_page)
    }

    companion object {

        @JvmField
        val CREATOR: Parcelable.Creator<Request> = object : Parcelable.Creator<Request> {
            override fun createFromParcel(`in`: Parcel): Request {
                val instance = Request()
                instance.q = `in`.readValue(String::class.java.classLoader) as? String
                instance.lang = `in`.readValue(String::class.java.classLoader) as?  String
                instance.image_type = `in`.readValue(String::class.java.classLoader) as?  String
                instance.orientation = `in`.readValue(String::class.java.classLoader) as?  String
                instance.category = `in`.readValue(String::class.java.classLoader) as?  String
                instance.min_width = `in`.readValue(Int::class.java.classLoader) as?  Int
                instance.min_height = `in`.readValue(Int::class.java.classLoader) as?  Int
                instance.colors = `in`.readValue(String::class.java.classLoader) as?  String
                instance.editors_choice = `in`.readValue(Boolean::class.java.classLoader) as?  Boolean
                instance.order = `in`.readValue(String::class.java.classLoader) as?  String
                instance.page = `in`.readValue(Int::class.java.classLoader) as?  Int
                instance.per_page = `in`.readValue(Int::class.java.classLoader) as?  Int

                return instance
            }

            override fun newArray(size: Int): Array<Request?> {
                return arrayOfNulls(size)
            }
        }
    }
}

