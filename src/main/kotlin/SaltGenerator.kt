fun saltGenerator(simbols:Int): String {
    val chars = "0123456789!@#*&?QqWwEeRrTtYyUuIiOoPpAaSsDdfFGhHjJkKlLZzXxCcvVbBNnMm"
    var salt = ""
    for (i in 0..simbols) {
        salt += chars[Math.floor(Math.random() * chars.length).toInt()]
    }
    return salt
}