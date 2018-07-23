import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun SQLUserNameUpdate(user_id:Int, name:String): Boolean {
    var result = false
    try {
        Class.forName("com.mysql.cj.jdbc.Driver")
        Database.connect(base_url, base_driver, base_root, base_pass)
        System.out.println("[MySQL] Connected")
        transaction {
            user_list.update({ user_list.user_id eq user_id}) {
                it[user_name] = name
                result = true
            }
        }
    }catch (e:ExceptionInInitializerError) {result = false}
    return result
}

fun SQLAvatarUpdate(user_id:Int, url:String): Boolean {
    var result = false
    try {
        Class.forName("com.mysql.cj.jdbc.Driver")
        Database.connect(base_url, base_driver, base_root, base_pass)
        System.out.println("[MySQL] Connected")
        transaction {
            user_list.update({ user_list.user_id eq user_id }) {
                it[avatar_url] = url
                result = true
            }
        }
    } catch (e:ExceptionInInitializerError) {result = false}
    return result
}

fun SQLPassUpdate(user_id:Int, new_pass:String, pass:String): Boolean {
    val salt1 = saltGenerator(6)
    val salt2 = saltGenerator(6)
    var result = false
    if (checkPass(user_id, pass))
        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
            Database.connect(base_url, base_driver, base_root, base_pass)
            System.out.println("[MySQL] Connected")
            transaction {
                user_list.update({ user_list.user_id eq user_id }) {
                    it[user_pass] = hashit(new_pass, salt1, salt2)
                    it[base_salt1] = salt1
                    it[base_salt2] = salt2
                    result = true
                }
            }
        } catch (e:ExceptionInInitializerError){result = false}
    return result
}

fun SQLEmailUpdate(user_id:Int, email:String): Boolean {
    var result = false
    if (isEmailValid(email)) {
        Class.forName("com.mysql.cj.jdbc.Driver")
        Database.connect(base_url, base_driver, base_root, base_pass)
        System.out.println("[MySQL] Connected")
        transaction {
            user_list.update({ user_list.user_id eq user_id }) {
                it[user_email] = email
                result = true
            }
        }
    } else result = false
    return result
}

fun SQLRoleUpdate(user_id:Int, new_role:String): Boolean {
    var result = false
    try {
        Class.forName("com.mysql.cj.jdbc.Driver")
        Database.connect(base_url, base_driver, base_root, base_pass)
        System.out.println("[MySQL] Connected")
        transaction {
            user_list.update({ user_list.user_id eq user_id }) {
                it[role] = new_role
                result = true
            }
        }
    } catch (e:ExceptionInInitializerError) {result = false}
    return result
}