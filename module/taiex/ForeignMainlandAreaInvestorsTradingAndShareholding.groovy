package module.taiex
class ForeignMainlandAreaInvestorsTradingAndShareholding{
	def sqlDirName = 'foreign_mainland_area_investors_trading_and_shareholding'
	def dbName = 'findb'
	def tableName_foreign_mainland_area_investors_trading_and_shareholding='foreign_mainland_area_investors_trading_and_shareholding'
	def doSync(){
//////////////
		module.processor.ProcessorRunner.runDayByDay{
			startYear Calendar.getInstance().get(Calendar.YEAR);
			startMonth Calendar.getInstance().get(Calendar.MONTH)+1
			startday 1
			endYear Calendar.getInstance().get(Calendar.YEAR);
			endMonth Calendar.getInstance().get(Calendar.MONTH)+1
			endDay Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
			process{yyyyMmDd->
			    if(new File("./${sqlDirName}/${yyyyMmDd}.sql").exists()){
			    	print '>'
			    }else{
					    def returnJson = module.web.Webget.download{
					         url "https://www.twse.com.tw/fund/MI_QFIIS?response=json&selectType=ALLBUT0999&date=${yyyyMmDd}"
					         decode 'utf-8'
					         validateShortSell true
					    }
					    //print returnJson
						def resultSql = module.parser.JsonConvert.convert{
				        	input returnJson
				        	parseRule {json->
					            if(json.stat != 'OK'){
					               return ''
					            }
					            def fields = json.fields.collect(fieldNormalize)

					            def _sql = "REPLACE INTO `${dbName}`.`${tableName_foreign_mainland_area_investors_trading_and_shareholding}` (`security_code`, `name_of_security`, `isin_code`, `number_of_shares_issued`, `available_shares_bought`, `currently_shares_held`, `percentage_of_available_investment`, `percentage_of_shares_held`, `upper_limit`, `reasons_of_change`, `last_filing_date_by_listed_companies`, `traded_day`) VALUES "
					            if(Integer.valueOf(yyyyMmDd)>=20090504){
					            	_sql = "REPLACE INTO `${dbName}`.`${tableName_foreign_mainland_area_investors_trading_and_shareholding}` (`security_code`, `name_of_security`, `isin_code`, `number_of_shares_issued`, `available_shares_bought`, `currently_shares_held`, `percentage_of_available_investment`, `percentage_of_shares_held`, `upper_limit`, `upper_limit_mainland`, `reasons_of_change`, `last_filing_date_by_listed_companies`, `traded_day`) VALUES "
					            }

					            for(int i=0;i<json.data.size;i++){
					               def _data = json.data[i].collect(valueNormalise).join("','");
					               if(i==0){
					                  _sql+="\r\n('${_data}','${yyyyMmDd}')"
					               }else{
					                   _sql+="\r\n,('${_data}','${yyyyMmDd}')"
					               }
					            }
					            return _sql
				        	}
				    	}

					    if(resultSql && !resultSql.endsWith('VALUES ')){
					    	module.io.Batch.exec{
                				mkdirs "./${sqlDirName}/"
                				write "./${sqlDirName}/${yyyyMmDd}.sql",'UTF-8',"${resultSql};"
           					}
							print '*'
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
		}
		println 'import foreign_mainland_area_investors_trading_and_shareholding done'
//////////////

	}
	def static sync(@DelegatesTo(ForeignMainlandAreaInvestorsTradingAndShareholding) Closure block){
	        ForeignMainlandAreaInvestorsTradingAndShareholding m = new ForeignMainlandAreaInvestorsTradingAndShareholding()
	        block.delegate = m
	        block()
	        m.doSync()
    }
}