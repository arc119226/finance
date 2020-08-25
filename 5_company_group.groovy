/**
 每日一次
*/
@Grab('mysql:mysql-connector-java:5.1.39')
@GrabConfig(systemClassLoader=true)
import groovy.sql.Sql

//EH000118
def s1 = module.web.Webget.download{
        url "https://stockchannelnew.sinotrade.com.tw/z/zg/zgd/zgd_EG00017_1.djhtm" 
}
s1 = s1.replaceAll(' selected','')
def sqlhead  = 'REPLACE INTO `stock_tw`.`company_group` (`group_code`,`group_name`) VALUES '
def pattern = ~/option value=\"(.+)">(.+)<\/option>/
def matcher = s1 =~ pattern
matcher.find()
matcher.each{data->
	println data
	if(sqlhead.endsWith('VALUES ')){
		sqlhead = sqlhead+"\r\n('${data[1]}','${data[2]}')"
	}else{
		 sqlhead = sqlhead+"\r\n,('${data[1]}','${data[2]}')"
	}
}

if(!sqlhead.endsWith('VALUES ')){
	def currentTime = new Date().getTime();
	new File('./company_group/').mkdir()
		new FileOutputStream('./company_group/'+currentTime+'.sql').withWriter('UTF-8') { writer ->
		writer << sqlhead+';'
	}
}
module.db.SqlExecuter.execute{
    dir './company_group'
}
module.io.FileBetch.execute{
    clean './company_group'
}

def sql = Sql.newInstance('jdbc:mysql://127.0.0.1:3306/stock_tw?useUnicode=yes&characterEncoding=UTF-8&character_set_server=utf8mb4',
						  'root',
						  'Esorn@ldorn110','com.mysql.jdbc.Driver')
def groups = sql.rows("select * from company_group ")


groups.each{group->
	println group.group_code

	def s2 = module.web.Webget.download{
	        url "https://stockchannelnew.sinotrade.com.tw/z/zg/zge/zge_${group.group_code}_1.djhtm"
	        retry 3
	        errorLog 'error.txt'
	        decode null
	        sleeptime 1000
	    }
	def pattern2 = ~/GenLink2stk\('(.+)'\,.+\);/
	def matcher2 = s2 =~ pattern2
	matcher2.find()
	def sqlhead2  = 'REPLACE INTO `stock_tw`.`company_stock` (`security_code`,`group_code`) VALUES '

	matcher2.each{code->
		def text = code[-1]
		text = text[2..-1]
		if(sqlhead2.endsWith('VALUES ')){
			sqlhead2 = sqlhead2+"\r\n('${text}','${group.group_code}')"
		}else{
		 	sqlhead2 = sqlhead2+"\r\n,('${text}','${group.group_code}')"
		}
	}
	// println sqlhead
	def currentTime = new Date().getTime();
	if(!sqlhead2.endsWith('VALUES ')){
		new File('./company_group/').mkdir()
 		new FileOutputStream('./company_group/'+currentTime+'.sql').withWriter('UTF-8') { writer ->
    		writer << sqlhead2+';'
 		}
	}
	sleep(100)
}
sql.close()

module.db.SqlExecuter.execute{
    dir './company_group'
}
module.io.FileBetch.execute{
    clean './company_group'
    delete './company_group'
}
println 'import company_group end'
// 

