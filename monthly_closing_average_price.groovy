@Grab('mysql:mysql-connector-java:5.1.39')
@GrabConfig(systemClassLoader=true)
import groovy.sql.Sql


static def deleter(){
	if(new File("./fail_url").exists()){
	new File("./fail_url").eachLine { line ->
	def fileName = line.split(' ')[0].trim()
		if(new File("./monthly_closing_average_price/${fileName}.sql").exists()){
			new File("./monthly_closing_average_price/${fileName}.sql").delete()
			println "delete ${fileName}.sql"
		}else{
			println "no file ${fileName}.sql"
		}
	}
	new File("./fail_url").delete()
	}

}
static def process_download(){
	def sql = Sql.newInstance('jdbc:mysql://127.0.0.1:3306/stock_tw?useUnicode=yes&characterEncoding=UTF-8&character_set_server=utf8mb4',
						  'root',
						  'Esorn@ldorn110','com.mysql.jdbc.Driver')

	def stockCodes = sql.rows("select stock.security_code,stock.listing_day from stock where stock.stock_type='上市' order by stock.listing_day")

	stockCodes.each{it->
		def security_code = it.security_code
		def listing_day = String.format("%04d%02d%02d",Integer.valueOf(it.listing_day.toString()[0..3]),Integer.valueOf(it.listing_day.toString()[4..5]),1)
		//println "${Integer.valueOf(listing_day)} vs 19990105"
		if(Integer.valueOf(listing_day) < 19990105){
			listing_day='19990101'
		}
		def resultSql =''
		if(!new File("./monthly_closing_average_price/${security_code}.sql").exists()){
			print listing_day
			module.processor.ProcessorRunner.runMonthByMonth{
				startYear Integer.valueOf(listing_day.toString()[0..3])
				startMonth Integer.valueOf(listing_day.toString()[4..5])
				startday Integer.valueOf(listing_day.toString()[6..7])
				endYear 2020
				endMonth 8
				endDay 1
				process{yyyyMmDd->
					sleep(50)
					def _url = "https://www.twse.com.tw/exchangeReport/STOCK_DAY_AVG?response=json&lang=en&stockNo=${security_code}&date=${yyyyMmDd}"
					print '.'
					def returnJson = module.web.Webget.download{
					     url _url
					     decode 'utf-8'
					     retry 10
					     sleeptime 250
					     validate true
					}
					module.parser.JsonConvert.convert{
					    input returnJson
						parseRule {json->
						    if(json.stat != 'OK'){
						    	print '.'
						    	return ''
					        }
					        def fields = json.fields.collect(fieldNormalize)
					        if(resultSql==''){
				            	resultSql = "REPLACE INTO `stock_tw`.`monthly_closing_average_price` (`${fields[0..1].join('`,`')}`,`traded_day`,`security_code`) VALUES "
						    }
						          
						    for(int i=0;i<json.data.size;i++){
						     	def _data = json.data[i].collect(valueNormalise)[0..1].join("','");
						         if(resultSql.endsWith('VALUES ')){
						        	def td = json.data[i][0].replaceAll(/^(\d+)(\/)(\d+)(\/)(\d+)$/,'$1$3$5');
						        	def md = Integer.valueOf(yyyyMmDd[0..5]+'31')
						        	if(Integer.valueOf(td) < Integer.valueOf(yyyyMmDd) || Integer.valueOf(td)>md){
						        		File error = new File('./fail_url')
	                					error.append("\n${security_code} ${td} ${yyyyMmDd} ${md}"+_url+' '+returnJson)
										return null;
									}
						        	resultSql+= "\r\n('${_data}','${td}','${security_code}')"
						        }else if(i==json.data.size-1){
						        	resultSql+= "\r\n,('${_data}','${yyyyMmDd}','${security_code}')"
						        }else{
						        	def td = json.data[i][0].replaceAll(/^(\d+)(\/)(\d+)(\/)(\d+)$/,'$1$3$5');
						        	def md = Integer.valueOf(yyyyMmDd[0..5]+'31')
						        	if(Integer.valueOf(td) < Integer.valueOf(yyyyMmDd) || Integer.valueOf(td) > md){
						        		File error = new File('./fail_url')
	                					error.append("\n${security_code} ${td} ${yyyyMmDd} ${md}"+_url+' '+returnJson)
										return null;
									}
						            resultSql+= "\r\n,('${_data}','${td}','${security_code}')"
						        }
						    }
						    //println ' parse json done'
						    return resultSql
						}
					}
				}//process
			}//run
			if(resultSql){
		    	new File('./monthly_closing_average_price/').mkdir()
		 		new FileOutputStream("./monthly_closing_average_price/${security_code}.tmp").withWriter('UTF-8') { writer ->
		    		writer << resultSql+';'
		 		}
		 		new File("./monthly_closing_average_price/${security_code}.tmp").renameTo("./monthly_closing_average_price/${security_code}.sql")
		 		//print ', save sql done'
			}else{

			}
			println security_code
		}else{
			println "${security_code} already done."
		}
	}

	// module.db.SqlExecuter.execute{
	//     dir './monthly_closing_average_price'
	// }
	// module.io.FileBetch.execute{
	// 	clean './monthly_closing_average_price'
	// }
	println 'import monthly_closing_average_price done'
}
static main(args) {
	deleter()
	process_download()
}
