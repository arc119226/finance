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
 	String queryString
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
	def queryString(String queryString){
		this.queryString=queryString
	}

	def insertOrReplace(){
		new File("${dir}").mkdir()
		def sql = Sql.newInstance(jdbc,user,pass,driver)
		new File(dir).list().each{ code ->
			println code
		    def text = new File("${dir}/${code}").text
		    if(text && !text.trim().endsWith('VALUES ;') && !text.trim().startsWith('!')&&!text.trim().startsWith(';')){
		        sql.execute(text)
		    }
		}
		sql.close()
	}
	def dsInstance(){
		return Sql.newInstance(jdbc,user,pass,driver)
	}
	def doQuery(){
		def sql = this.dsInstance()
		def result = sql.rows(queryString)
		sql.close()
		return result
	}
	def static execute(@DelegatesTo(SqlExecuter) Closure block){
		SqlExecuter m = new SqlExecuter()
		block.delegate = m
		block()
		m.insertOrReplace()
	}

	def static dbConnection(@DelegatesTo(SqlExecuter) Closure block){
		SqlExecuter m = new SqlExecuter()
		block.delegate = m
		block()
		return m.dsInstance()
	}
	def static query(@DelegatesTo(SqlExecuter) Closure block){
		SqlExecuter m = new SqlExecuter()
		block.delegate = m
		block()
		return m.doQuery()
	}

}

