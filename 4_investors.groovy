/**
 每日一次
*/
for (item in [[type:"FD",code:'TWT38U'],[type:"SITC",code:"TWT44U"],[type:"D",code:"TWT44U"]]) {
	module.processor.ProcessorRunner.runDayByDay{
		startYear Calendar.getInstance().get(Calendar.YEAR)
		startMonth Calendar.getInstance().get(Calendar.MONTH)+1Calendar.getInstance().get(Calendar.MONTH)+1
		startday Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
		endYear Calendar.getInstance().get(Calendar.YEAR)
		endMonth Calendar.getInstance().get(Calendar.MONTH)+1
		endDay Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
		process{yyyyMmDd->
			if(new File('./investors/${item.type}${yyyyMmDd}.sql').exists()){
				print '>'
			}else{
				def returnJson = module.web.Webget.download{
			    	url "https://www.twse.com.tw/fund/${item.code}?response=json&lang=en&date=${yyyyMmDd}"
			    	decode 'utf-8'
			    	validateInv true
				}
				def resultSql = module.parser.JsonConvert.convert{
			    	input returnJson
					parseRule {json->
				    if(json.stat != 'OK' && json.date !="${yyyyMmDd}"){
			           return null
			        }
			        def fields = json.fields.collect(fieldNormalize)
				    def _sql = "REPLACE INTO `stock_tw`.`investors` (`${fields[1..4].join('`,`')}`,`traded_day`,`type`) VALUES "
				            
				     for(int i=0;i<json.data.size;i++){
				     	def _data = json.data[i].collect(valueNormalise)[1..4].join("','");
				        if(i==0){
				        	_sql+="\r\n('${_data}','${yyyyMmDd}','${item.type}')"
				        }else{
				            _sql+="\r\n,('${_data}','${yyyyMmDd}','${item.type}')"
				        }
				     }
				     return _sql
					}
				}

		    	if(resultSql && !resultSql.endsWith('VALUES ')){
		    		new File('./investors/').mkdir()
	     			new FileOutputStream("./investors/${item.type}${yyyyMmDd}.tmp").withWriter('UTF-8') { writer ->
	        			writer << resultSql+';'
	     			}
	     			new File("./investors/${item.type}${yyyyMmDd}.tmp").renameTo("./investors/${item.type}${yyyyMmDd}.sql")
	     			print '*'
				}
			}
		}
	}
}

module.db.SqlExecuter.execute{
    dir './investors'
}
module.io.FileBetch.execute{
	clean './investors'
	delete './investors'
}
println 'import investors done'