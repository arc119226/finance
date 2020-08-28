package module.taiex
class MarginTransactionl{
	def sqlDirName = 'margin_transactionl'
	def doSync(){
/////////////////////
		module.processor.ProcessorRunner.runDayByDay{
			startYear Calendar.getInstance().get(Calendar.YEAR)
			startMonth Calendar.getInstance().get(Calendar.MONTH)+1
			startday 1//Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
			endYear Calendar.getInstance().get(Calendar.YEAR)
			endMonth Calendar.getInstance().get(Calendar.MONTH)+1
			endDay Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
			process{yyyyMmDd->
			    if(new File("./${sqlDirName}/${yyyyMmDd}.sql").exists() && new File('./margin_transactionl/'+yyyyMmDd+'_all.sql').exists()){
			    	print '>'
			    }else{
					    def returnJson = module.web.Webget.download{
					         url "https://www.twse.com.tw/exchangeReport/MI_MARGN?response=json&lang=en&selectType=ALL&date=${yyyyMmDd}"
					         decode 'utf-8'
					         validateMarginTransactionl true
					    }

						def resultSql = module.parser.JsonConvert.convert{
				        	input returnJson
				        	parseRule {json->
					            if(json.stat != 'OK'){
					               return ''
					            }
					            def _sql = "REPLACE INTO `stock_tw`.`margin_transactions_all` (`security_code`,`margin_purchase`,`margin_sales`,`cash_redemption`,`balance_of_previous_day`,`balance_of_the_day`,`quota`,`short_covering`,`short_sale`,`stock_redemption`,`short_balance_of_previous_day`,`short_balance_of_the_day`,`short_quota`,`offsetting_of_margin_purchases_and_short_sales`,`note`,`traded_day`) VALUES "
					            
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
                				write "./${sqlDirName}/${yyyyMmDd}_all.tmp",'UTF-8',"${resultSql};"
                				rename "./${sqlDirName}/${yyyyMmDd}_all.tmp","./${sqlDirName}/${yyyyMmDd}_all.sql"
           					}
							print '#'
						}

						def resultSqlSummary = module.parser.JsonConvert.convert{
				        	input returnJson
				        	parseRule {json->
					            if(json.stat != 'OK'){
					               return ''
					            }
					            def fields = json.creditFields.collect(fieldNormalize)
					            def _sql = "REPLACE INTO `stock_tw`.`margin_transaction_summary` (`${fields.join('`,`')}`,`traded_day`) VALUES "
					            
					            for(int i=0;i<json.creditList.size;i++){
					               def _data = json.creditList[i].collect(valueNormalise).join("','");
					               if(i==0){
					                  _sql+="\r\n('${_data}','${yyyyMmDd}')"
					               }else{
					                   _sql+="\r\n,('${_data}','${yyyyMmDd}')"
					               }
					            }
					            return _sql
				        	}
				    	}
					    if(resultSqlSummary && !resultSqlSummary.endsWith('VALUES ')){
				     		module.io.Batch.exec{
                				mkdirs "./${sqlDirName}/"
                				write "./${sqlDirName}/${yyyyMmDd}_summary.tmp",'UTF-8',"${resultSql};"
                				rename "./${sqlDirName}/${yyyyMmDd}_summary.tmp","./${sqlDirName}/${yyyyMmDd}_summary.sql"
           					}
							print '*'
						}
			    }
			}
		}

		module.db.SqlExecuter.execute{
		    dir "./${sqlDirName}"
		}
		module.io.Batch.execute{
			clean "./${sqlDirName}"
			delete "./${sqlDirName}"
			info 'import margin_transactionl done'
		}
		println 
////////////////////
	}
	def static sync(@DelegatesTo(MarginTransactionl) Closure block){
	        MarginTransactionl m = new MarginTransactionl()
	        block.delegate = m
	        block()
	        m.doSync()
    }	
}