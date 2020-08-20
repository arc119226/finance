/**
 1.download worth day json
 2.convert json to sql
 3.import sql
*/
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
	         url "https://www.twse.com.tw/exchangeReport/BWIBBU_d?response=json&selectType=ALL&lang=en&date=${yyyyMmDd}"
	         decode 'utf-8'
	    }
	    print ' fetch api done'

		def resultSql = module.parser.JsonConvert.convert{
        	input returnJson
        	parseRule {json->
	            if(json.stat != 'OK'){
	               return null
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
	            print ', parse json done'
	            return _sql
        	}
    	}

	    if(resultSql){
	    	new File('./pe_dy_pb/').mkdir()
     		new FileOutputStream('./pe_dy_pb/'+yyyyMmDd+'.tmp').withWriter('UTF-8') { writer ->
        		writer << resultSql+';'
     		}
     		new File('./pe_dy_pb/'+yyyyMmDd+'.tmp').renameTo('./pe_dy_pb/'+yyyyMmDd+'.sql')
     		print ', save sql done'
			println ''
		}
	}
}

module.db.SqlExecuter.execute{
    dir './pe_dy_pb'
}
println 'import pe_dy_pb done'
// FileUtils.cleanDirectory(new File('worth_sql'))
// FileUtils.cleanDirectory(new File('worth_history'))
