val useTestingPuppet = true

val SQL = when (useTestingPuppet) {
    true -> PuppetClass
    else -> BaseClass
}
object TestingPuppet {
    val fullData = UserFullData(
            userID = 1,
            userEmail = "example@email.pp",
            userName = "Testing_Puppet_Name",
            avatarURL = "Puppet_URL",
            role = "Puppet",
            mute = false)
    val publicData = UserPublicData(
           fullData.userEmail!!,
            fullData.userName!!,
            fullData.avatarURL!!,
            fullData.role!!,
            fullData.mute!!)
    val pass = "12345"
    val puppetList = listOf(publicData,publicData,publicData,publicData,publicData)
    val fullPuppetList = listOf(fullData,fullData,fullData,fullData,fullData)
    val dataTypes = listOf("email","role", "username","password","avatarURL","mute")
}