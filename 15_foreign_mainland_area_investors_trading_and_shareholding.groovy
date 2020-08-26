/**
 每日一次
 1.download worth day json
 2.convert json to sql
 3.import sql
*/
module.processor.ProcessorRunner.runDayByDay{
	startYear Calendar.getInstance().get(Calendar.YEAR);
	startMonth Calendar.getInstance().get(Calendar.MONTH)+1
	startday 1
	endYear Calendar.getInstance().get(Calendar.YEAR);
	endMonth Calendar.getInstance().get(Calendar.MONTH)+1
	endDay Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
	process{yyyyMmDd->
	    if(new File('./foreign_mainland_area_investors_trading_and_shareholding/'+yyyyMmDd+'.sql').exists()){
	    	print '>'
	    }else{
			    def returnJson = module.web.Webget.download{
			         url "https://www.twse.com.tw/fund/MI_QFIIS?response=json&selectType=ALLBUT0999&date=${yyyyMmDd}"
			         decode 'utf-8'
			         validateShortSell true
			    }
			    print returnJson
				def resultSql = module.parser.JsonConvert.convert{
		        	input returnJson
		        	parseRule {json->
			            if(json.stat != 'OK'){
			               return ''
			            }
			            def fields = json.fields.collect(fieldNormalize)

			            def _sql = "REPLACE INTO `stock_tw`.`foreign_mainland_area_investors_trading_and_shareholding` (`security_code`, `name_of_security`, `isin_code`, `number_of_shares_issued`, `available_shares_bought`, `currently_shares_held`, `percentage_of_available_investment`, `percentage_of_shares_held`, `upper_limit`, `reasons_of_change`, `last_filing_date_by_listed_companies`, `traded_day`) VALUES "
			            if(Integer.valueOf(yyyyMmDd)>=20090504){
			            	_sql = "REPLACE INTO `stock_tw`.`foreign_mainland_area_investors_trading_and_shareholding` (`security_code`, `name_of_security`, `isin_code`, `number_of_shares_issued`, `available_shares_bought`, `currently_shares_held`, `percentage_of_available_investment`, `percentage_of_shares_held`, `upper_limit`, `upper_limit_mainland`, `reasons_of_change`, `last_filing_date_by_listed_companies`, `traded_day`) VALUES "
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
			    	new File('./foreign_mainland_area_investors_trading_and_shareholding/').mkdir()
		     		new FileOutputStream('./foreign_mainland_area_investors_trading_and_shareholding/'+yyyyMmDd+'.tmp').withWriter('UTF-8') { writer ->
		        		writer << resultSql+';'
		     		}
		     		new File('./foreign_mainland_area_investors_trading_and_shareholding/'+yyyyMmDd+'.tmp').renameTo('./foreign_mainland_area_investors_trading_and_shareholding/'+yyyyMmDd+'.sql')
					print '*'
				}
	    }
	}
}

module.db.SqlExecuter.execute{
    dir './foreign_mainland_area_investors_trading_and_shareholding'
}
module.io.FileBetch.execute{
	clean './foreign_mainland_area_investors_trading_and_shareholding'
	delete './foreign_mainland_area_investors_trading_and_shareholding'
}
println 'import foreign_mainland_area_investors_trading_and_shareholding done'

