package dreamlab.worldpics.model

import java.io.Serializable

data class PhotoRequest(
    var q: String? = null,
    var lang: String? = null,
    var image_type: String? = null,
    var orientation: String? = null,
    var category: String? = null,
    var min_width: Int? = null,
    var min_height: Int? = null,
    var colors: String? = null,
    var editors_choice: Boolean = false,
    var order: String? = null,
    var page: Int? = 0,
    var per_page: Int? = 20
) : Serializable {

    data class Builder(
        var q: String? = null,
        var language: String? = null,
        var image_type: String? = null,
        var orientation: String? = null,
        var category: String? = null,
        var min_width: Int? = null,
        var min_height: Int? = null,
        var colors: String? = null,
        var editors_choice: Boolean = false,
        var order: String? = null,
        var page: Int? = null,
        var per_page: Int? = null
    ) {
        fun q(q: String) = apply { this.q = q }
        fun orientation(orientation: String?) = apply { this.orientation = orientation }
        fun category(category: String?) = apply { this.category = category }
        fun colors(colors: String?) = apply { this.colors = colors }
        fun editorsChoice(editorsChoice: Boolean) = apply { this.editors_choice = editorsChoice }

        fun build(): PhotoRequest {
            return PhotoRequest(
                q,
                language,
                image_type,
                orientation,
                category,
                min_width,
                min_height,
                colors,
                editors_choice,
                order,
                page,
                per_page
            )
        }
    }
}

