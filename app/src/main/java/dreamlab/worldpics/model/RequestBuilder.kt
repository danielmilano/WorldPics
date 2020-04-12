package dreamlab.worldpics.model

class RequestBuilder() : Builder {

    private var q: String? = null
    private var language: String? = null
    private var image_type: String? = null
    private var orientation: String? = null
    private var category: String? = null
    private var min_width: Int? = null
    private var min_height: Int? = null
    private var colors: String? = null
    private var editorsChoice: Boolean = false
    private var order: String? = null
    private var page: Int? = null
    private var per_page: Int? = null

    override fun setQ(query: String) {
        this.q = query
    }

    override fun setLanguage(language: String) {
        this.language = language
    }

    override fun setImageType(image_type: String) {
        this.image_type = image_type
    }

    override fun setOrientation(orientation: String) {
        this.orientation = orientation
    }

    override fun setCategory(category: String) {
        this.category = category
    }

    override fun setMin_width(min_width: Int?) {
        this.min_width = min_width
    }

    override fun setMin_height(min_height: Int?) {
        this.min_height = min_height
    }

    override fun setColors(colors: String) {
        this.colors = colors
    }

    override fun setEditorsChoice(editorsChoice: Boolean) {
        this.editorsChoice = editorsChoice
    }

    override fun setOrder(order: String) {
        this.order = order
    }

    override fun setPage(page: Int?) {
        this.page = page
    }

    override fun setPerPage(per_page: Int?) {
        this.per_page = per_page
    }

    fun build(): Request {
        return Request(
            q,
            language,
            image_type,
            orientation,
            category,
            min_width,
            min_height,
            colors,
            editorsChoice,
            order,
            page,
            per_page
        )
    }
}
