import java.security.MessageDigest

fun hashit(pass:String,salt1:String,salt2:String): String {
    return hasher(salt1 + pass + salt2)
}

private fun hasher(text: String ): String {                // Основная функция, хеширующая пароль
    var result: String
    try {
        val sha256 = MessageDigest.getInstance("SHA-256")
        val sha256HashBytes = sha256.digest(text.toByteArray()).toTypedArray()
        result = byteToHex(sha256HashBytes)
    }
    catch ( e: Exception ) {
        result = "[Hasher] ${e.message}"
        System.out.println(result)
    }
    return result
}

private fun byteToHex( array: Array<Byte> ): String {      //Перевод в HEX (СПАСИБО AllTech за эту функцию)
    val result = StringBuilder(array.size * 2)
    for ( byte in array ) {
        val toAppend = String.format("%2X", byte).replace(" ", "0")
        result.append(toAppend).append("")
    }
    result.setLength(result.length - 1)
    return result.toString()
}