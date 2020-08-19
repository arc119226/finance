//FD SITC D
def returnJson = module.web.Webget.download{
     url "https://www.twse.com.tw/fund/TWT38U?response=json&lang=en&date=${yyyyMmDd}"
     decode 'utf-8'
}
def resultSql = module.parser.JsonConvert.convert{
    input returnJson
	parseRule {json->
	    if(json.stat != 'OK'){
           return null
        }
        def fields = json.fields.collect(fieldNormalize)
        println fields[1..4].join('`,`')
	    def _sql = "REPLACE INTO `stock_tw`.`pe_dy_pb` (`${fields[1..4].join('`,`')}`,`traded_day`,`type`) VALUES "
	            
	     for(int i=0;i<json.data.size;i++){
	     	def _data = json.data[i].collect(valueNormalise)[1..4].join("','");
	        if(i==0){
	        	_sql+="\r\n('${_data}','${yyyyMmDd}','FD')"
	        }else{
	            _sql+="\r\n,('${_data}','${yyyyMmDd}''FD')"
	        }
	     }
	     print ', parse json done'
	     return _sql
	}
}
