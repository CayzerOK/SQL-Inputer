import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun sqlEmailUpdate(email:String,id:Int) {
    if (isEmailValid(email)) {
        Class.forName("com.mysql.cj.jdbc.Driver")
        Database.connect(base_url, base_driver, base_root, base_pass)
        System.out.println("[MySQL] Connected")
        transaction {
            user_list.update({ user_list.user_id eq id}) {
                it[user_email] = email
            }
        }
    }
}

fun sqlUserNameUpdate(name:String,id:Int) {
    Class.forName("com.mysql.cj.jdbc.Driver")
    Database.connect(base_url, base_driver, base_root, base_pass)
    System.out.println("[MySQL] Connected")
    transaction {
        user_list.update({ user_list.user_id eq id}) {
            it[user_name] = name
        }
    }
}

fun sqlPassUpdate(pass:String,id:Int) {
    val salt1 = saltGenerator(6)
    val salt2 = saltGenerator(6)
    Class.forName("com.mysql.cj.jdbc.Driver")
    Database.connect(base_url, base_driver, base_root, base_pass)
    System.out.println("[MySQL] Connected")
    transaction {
        user_list.update({ user_list.user_id eq id}) {
            it[user_pass] = hashit(pass, salt1, salt2)
            it[base_salt1] = salt1
            it[base_salt2] = salt2
        }
    }
}

fun sqlAvatarUpdate(url:String,id:Int) {
    Class.forName("com.mysql.cj.jdbc.Driver")
    Database.connect(base_url, base_driver, base_root, base_pass)
    System.out.println("[MySQL] Connected")
    transaction {
        user_list.update({ user_list.user_id eq id}) {
            it[avatar_url] = url
        }
    }
}