package dreamlab.worldpics.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Feedback(var message: String, var version: String, var buildSDKVersion: Int)
