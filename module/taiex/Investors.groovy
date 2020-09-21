package module.taiex
/**
三大法人買賣超
*/
class Investors{
	def sqlDirName='investors'
	def dbName = 'findb'
	def tableName_investors = 'investors'
	def doSync(){
		for (item in [[type:"FD",code:'TWT38U'],[type:"SITC",code:"TWT44U"],[type:"D",code:"TWT44U"]]) {
			module.processor.ProcessorRunner.runDayByDay{
				startYear Calendar.getInstance().get(Calendar.YEAR)
				startMonth Calendar.getInstance().get(Calendar.MONTH)+1
				startday 1//Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
				endYear Calendar.getInstance().get(Calendar.YEAR)
				endMonth Calendar.getInstance().get(Calendar.MONTH)+1
				endDay Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
				process{yyyyMmDd->

					if(new File("./${sqlDirName}/${item.type}${yyyyMmDd}.sql").exists()){
						print '>'
					}else{
						 def z = [2500,2600,2700,2800,2900,3000]
						Random rnd = new Random()
						def w = z[rnd.nextInt(z.size())]
						println 'wait'+ w
						sleep(w)
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
						    def _sql = "REPLACE INTO `${dbName}`.`${tableName_investors}` (`${fields[1..4].join('`,`')}`,`traded_day`,`type`) VALUES "
						            
						     for(int i=0;i<json.data.size();i++){
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
					    	module.io.Batch.exec{
		                		mkdirs "./${sqlDirName}"
		                		write "./${sqlDirName}/${item.type}${yyyyMmDd}.sql",'UTF-8',"${resultSql};"
		           			}//exec
		           			print '*'
						}else{
							module.io.Batch.exec{
		                		mkdirs "./${sqlDirName}"
		                		write "./${sqlDirName}/${item.type}${yyyyMmDd}.sql",'UTF-8',";"
		           			}
						}
					}
				}
			}
		}

		module.db.SqlExecuter.exec{
		    dir "./${sqlDirName}"
		}
		module.io.Batch.exec{
			clean "./${sqlDirName}"
			delete "./${sqlDirName}"
			info 'import investors done'
		} 
	}
    def static sync(@DelegatesTo(Investors) Closure block){
	        Investors m = new Investors()
	        block.delegate = m
	        block()
	        m.doSync()
	}
}