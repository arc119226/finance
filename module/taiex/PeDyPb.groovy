package module.taiex
/**
本益比 
判斷公司股價是否合理，通常用來分析獲利穩定的公司。
淨值比
判斷公司股價是否合理，通常用來分析獲利不穩定或虧損的公司。
殖利率
年報酬率
*/
class PeDyPb{

	def sqlDirName='pe_dy_pb'
	def dbName = 'findb'
	def tableName = 'pe_dy_pb'

	def doSync(){
		module.processor.ProcessorRunner.runDayByDay{
			startYear Calendar.getInstance().get(Calendar.YEAR)
			startMonth Calendar.getInstance().get(Calendar.MONTH)+1
			startday 1//Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
			endYear Calendar.getInstance().get(Calendar.YEAR)
			endMonth Calendar.getInstance().get(Calendar.MONTH)+1
			endDay Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
			process{yyyyMmDd->
		    	if(new File("./${sqlDirName}/${yyyyMmDd}.sql").exists()){
		    		print '>'
		    	}else{
		    		sleep(2800)
				    def returnJson = module.web.Webget.download{
				         url "https://www.twse.com.tw/exchangeReport/BWIBBU_d?response=json&selectType=ALL&lang=en&date=${yyyyMmDd}"
				         decode 'utf-8'
				         validatePb true
				    }//download

					def resultSql = module.parser.JsonConvert.convert{
			        	input returnJson
			        	parseRule {json->
				            if(json.stat != 'OK'){
				               return ''
				            }
				            def fields = json.fields.collect(fieldNormalize)

				            def _sql = "REPLACE INTO `${dbName}`.`${tableName}` (`${fields.join('`,`')}`,`traded_day`) VALUES "
				            
				            for(int i=0;i<json.data.size();i++){
				               def _data = json.data[i].collect(valueNormalise).join("','");
				               if(i==0){
				                  _sql+="\r\n('${_data}','${yyyyMmDd}')"
				               }else{
				                   _sql+="\r\n,('${_data}','${yyyyMmDd}')"
				               }//else
				            }//for
				            return _sql
			        	}//rule
			    	}//convert

					if(resultSql && !resultSql.endsWith('VALUES ')){
				    	module.io.Batch.exec{
	                		mkdirs "./${sqlDirName}"
	                		write "./${sqlDirName}//${yyyyMmDd}.sql",'UTF-8',"${resultSql};"
	           			}//exec
	           			print '*'
					}//if
				}//else
			}//process
		}//runDayByDay
		module.db.SqlExecuter.exec{
		    dir "./${sqlDirName}"
		}
		module.io.Batch.exec{
			clean "./${sqlDirName}"
			delete "./${sqlDirName}"
			info 'import pe_dy_pb done'
		}
 	}

	def static sync(@DelegatesTo(PeDyPb) Closure block){
            PeDyPb m = new PeDyPb()
            block.delegate = m
            block()
            m.doSync()

    }
}