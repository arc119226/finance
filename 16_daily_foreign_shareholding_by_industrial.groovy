/**
 每日一次
 1.download worth day json
 2.convert json to sql
 3.import sql
*/
module.processor.ProcessorRunner.runDayByDay{
	startYear Calendar.getInstance().get(Calendar.YEAR)
	startMonth Calendar.getInstance().get(Calendar.MONTH)+1
	startday Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
	endYear Calendar.getInstance().get(Calendar.YEAR)
	endMonth Calendar.getInstance().get(Calendar.MONTH)+1
	endDay Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
	process{yyyyMmDd->
	    if(new File('./daily_foreign_shareholding_by_industrial/'+yyyyMmDd+'.sql').exists()){
	    	print '>'
	    }else{
			    def returnJson = module.web.Webget.download{
			         url "https://www.twse.com.tw/fund/MI_QFIIS_cat?response=json&lang=en&date=${yyyyMmDd}"
			         decode 'utf-8'
			         validateIndustrial true
			    }

				def resultSql = module.parser.JsonConvert.convert{
		        	input returnJson
		        	parseRule {json->
			            if(json.stat != 'OK'){
			               return ''
			            }
			            def fields = json.fields.collect(fieldNormalize)

			            def _sql = "REPLACE INTO `stock_tw`.`daily_foreign_shareholding_by_industrial` (`category_of_Industry`,`numbers`,`number_of_shares_issued`,`currently_foreign_and_mainland_area_shares_held`,`percentage_of_foreign_and_mainland_area_shares_held`,`traded_day`) VALUES "
			            
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
			    	new File('./daily_foreign_shareholding_by_industrial/').mkdir()
		     		new FileOutputStream('./daily_foreign_shareholding_by_industrial/'+yyyyMmDd+'.tmp').withWriter('UTF-8') { writer ->
		        		writer << resultSql+';'
		     		}
		     		new File('./daily_foreign_shareholding_by_industrial/'+yyyyMmDd+'.tmp').renameTo('./daily_foreign_shareholding_by_industrial/'+yyyyMmDd+'.sql')
					print '*'
				}
	    }
	}
}

module.db.SqlExecuter.execute{
    dir './daily_foreign_shareholding_by_industrial'
}
module.io.FileBetch.execute{
	clean './daily_foreign_shareholding_by_industrial'
	delete './daily_foreign_shareholding_by_industrial'
}
println 'import daily_foreign_shareholding_by_industrial done'

