for (item in [[type:"FD",code:'TWT38U'],[type:"SITC",code:"TWT44U"],[type:"D",code:"TWT44U"]]) {
	module.processor.ProcessorRunner.runDayByDay{
		startYear 2020
		startMonth 8
		startday 1
		endYear 2020
		endMonth 8
		endDay 20
		process{yyyyMmDd->
			println ''
		    print yyyyMmDd
			def returnJson = module.web.Webget.download{
			     url "https://www.twse.com.tw/fund/${item.code}?response=json&lang=en&date=${yyyyMmDd}"
			     decode 'utf-8'
			}
			def resultSql = module.parser.JsonConvert.convert{
			    input returnJson
				parseRule {json->
				    if(json.stat != 'OK'){
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
				     print ', parse json done'
				     return _sql
				}
			}

		    if(resultSql){
		    	new File('./investors/').mkdir()
	     		new FileOutputStream("./investors/${item.type}${yyyyMmDd}.tmp").withWriter('UTF-8') { writer ->
	        		writer << resultSql+';'
	     		}
	     		new File("./investors/${item.type}${yyyyMmDd}.tmp").renameTo("./investors/${item.type}${yyyyMmDd}.sql")
	     		print ', save sql done'
			}
		}
	}
}

module.db.SqlExecuter.execute{
    dir './investors'
}
module.io.FileBetch.execute{
	clean './investors'
}
println 'import investors done'