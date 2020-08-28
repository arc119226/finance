package module.taiex

class ConceptStock{
	def sqlDirName = 'concept_stock'

	def doSync(){
		///
		def s1 = module.web.Webget.download{
		        url "https://stockchannelnew.sinotrade.com.tw/z/zg/zge/zge_EH000118_1.djhtm"
		}
		s1 = s1.replaceAll(' selected','')
		def sqlhead  = 'REPLACE INTO `stock_tw`.`concept_group` (`group_code`,`group_name`) VALUES '
		def pattern = ~/option value=\"(.+)">(.+)<\/option>/
		def matcher = s1 =~ pattern
		matcher.find()
		matcher.each{data->
			println data
			if(sqlhead.endsWith('VALUES ')){
				sqlhead = sqlhead+"\r\n('${data[1]}','${data[2]}')"
			}else{
				 sqlhead = sqlhead+"\r\n,('${data[1]}','${data[2]}')"
			}
		}

		if(!sqlhead.endsWith('VALUES ')){
			def currentTime = new Date().getTime();
			module.io.Batch.exec{
		 			mkdirs "./${sqlDirName}"
		 			write "./${sqlDirName}/${currentTime}.tmp",'UTF-8',"${sqlhead};"
                	rename "./${sqlDirName}/${currentTime}.tmp","./${sqlDirName}/${currentTime}.sql" 
		 	}
		}
		println sqlhead
		module.db.SqlExecuter.execute{
		    dir "./${sqlDirName}"
		}
		module.io.Batch.exec{
		    clean "./${sqlDirName}"
		}

		def sql = module.db.SqlExecuter.dbConnection{}
		def groups = sql.rows("select * from concept_group ")
		sql.close()
		groups.each{group->
			println group.group_code
			def s2 = module.web.Webget.download{
			        url "https://stockchannelnew.sinotrade.com.tw/z/zg/zge/zge_${group.group_code}_1.djhtm"
			    }
			def pattern2 = ~/GenLink2stk\('(.+)'\,.+\);/
			def matcher2 = s2 =~ pattern2
			matcher2.find()
			def sqlhead2  = 'REPLACE INTO `stock_tw`.`concept_stock` (`security_code`,`group_code`) VALUES '

			matcher2.each{code->
				def text = code[-1]
				text = text[2..-1]
				if(sqlhead2.endsWith('VALUES ')){
					sqlhead2 = sqlhead2+"\r\n('${text}','${group.group_code}')"
				}else{
				 	sqlhead2 = sqlhead2+"\r\n,('${text}','${group.group_code}')"
				}
			}
			// println sqlhead
			def currentTime = new Date().getTime();
			if(!sqlhead2.endsWith('VALUES ')){
		 		module.io.Batch.exec{
		 			mkdirs "./${sqlDirName}"
		 			write "./${sqlDirName}/${currentTime}.tmp",'UTF-8',"${sqlhead2};"
                	rename "./${sqlDirName}/${currentTime}.tmp","./${sqlDirName}/${currentTime}.sql" 
		 		}
			}
			sleep(100)
		}

		module.db.SqlExecuter.execute{
		    dir "./${sqlDirName}"
		}
		module.io.Batch.exec{
		    clean "./${sqlDirName}"
		    delete "./${sqlDirName}"
		    info 'import concept_stock end'
		} 



		///
	}

	def static sync(@DelegatesTo(ConceptStock) Closure block){
	        ConceptStock m = new ConceptStock()
	        block.delegate = m
	        block()
	        m.doSync()
    }
}