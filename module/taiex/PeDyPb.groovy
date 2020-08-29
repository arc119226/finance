package module.taiex
class PeDyPb{

	def sqlDirName='pe_dy_pb'

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

				            def _sql = "REPLACE INTO `stock_tw`.`pe_dy_pb` (`${fields.join('`,`')}`,`traded_day`) VALUES "
				            
				            for(int i=0;i<json.data.size;i++){
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
	                		write "./${sqlDirName}//${yyyyMmDd}.tmp",'UTF-8',"${resultSql};"
	                		rename "./${sqlDirName}/${yyyyMmDd}.tmp","./${sqlDirName}/${yyyyMmDd}.sql"
	           			}//exec
	           			print '*'
					}//if
				}//else
			}//process
		}//runDayByDay
		module.db.SqlExecuter.execute{
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