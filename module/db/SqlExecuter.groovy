package module.db
@Grab('mysql:mysql-connector-java:5.1.39')
@GrabConfig(systemClassLoader=true)
import groovy.sql.Sql

class SqlExecuter{
	String driver='com.mysql.jdbc.Driver'
	String jdbc='jdbc:mysql://127.0.0.1:3306/stock_tw?useUnicode=yes&characterEncoding=UTF-8&character_set_server=utf8mb4'
	String user='root'
	String pass='Esorn@ldorn110'
	String dir
 
	def driver(String obj){
		this.driver=obj
	}
	def jdbc(String obj){
		this.jdbc=obj
	}
	def user(String obj){
		this.user=obj
	}
	def pass(String obj){
		this.pass=obj
	}
	def dir(String obj){
		this.dir=obj
	}

	def insertOrReplace(){
		println dir
		new File("${dir}").mkdir()
		def sql = Sql.newInstance(jdbc,user,pass,driver)
		new File(dir).list().each{ code ->
		    def text = new File("${dir}/${code}").text
		    if(!text.trim().endsWith('VALUES;')){
		        sql.execute(text)
		        println code
		    }
		}
		sql.close()
	}
	def static execute(@DelegatesTo(SqlExecuter) Closure block){
		SqlExecuter m = new SqlExecuter()
		block.delegate = m
		block()
		m.insertOrReplace()
	}

}

