package dreamlab.worldpics.model

interface Builder {

    fun setQ(query: String)
    fun setLanguage(language: String)
    fun setImageType(image_type: String?)
    fun setOrientation(orientation: String?)
    fun setCategory(category: String?)
    fun setMin_width(min_width: Int?)
    fun setMin_height(min_height: Int?)
    fun setColors(colors: String?)
    fun setEditorsChoice(editorsChoice: Boolean)
    fun setOrder(order: String)
    fun setPage(page: Int?)
    fun setPerPage(per_page: Int?)

}
