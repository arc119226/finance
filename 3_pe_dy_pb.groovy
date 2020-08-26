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
	    if(new File('./pe_dy_pb/'+yyyyMmDd+'.sql').exists()){
	    	print '>'
	    }else{
			    def returnJson = module.web.Webget.download{
			         url "https://www.twse.com.tw/exchangeReport/BWIBBU_d?response=json&selectType=ALL&lang=en&date=${yyyyMmDd}"
			         decode 'utf-8'
			         validatePb true
			    }

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
			               }
			            }
			            return _sql
		        	}
		    	}

			    if(resultSql && !resultSql.endsWith('VALUES ')){
			    	new File('./pe_dy_pb/').mkdir()
		     		new FileOutputStream('./pe_dy_pb/'+yyyyMmDd+'.tmp').withWriter('UTF-8') { writer ->
		        		writer << resultSql+';'
		     		}
		     		new File('./pe_dy_pb/'+yyyyMmDd+'.tmp').renameTo('./pe_dy_pb/'+yyyyMmDd+'.sql')
					print '*'
				}
	    }
	}
}

module.db.SqlExecuter.execute{
    dir './pe_dy_pb'
}
module.io.FileBetch.execute{
	clean './pe_dy_pb'
	delete './pe_dy_pb'
}
println 'import pe_dy_pb done'

