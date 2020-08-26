package module.io
@Grab(group='commons-io', module='commons-io', version='2.5')
import org.apache.commons.io.FileUtils
class FileBetch{
	def storage = [:]

	def propertyMissing(String name) { 
		return storage[name] 
	}

	def methodMissing(String name, def args) {
		if(args.length==1){
			storage[name]=args[0]
		}else{
			storage[name]=args
		}
        return storage[name]
   }

	def executer(){
		if(clean){
			if([Collection, Object[]].any { it.isAssignableFrom(clean.getClass()) }){
				clean.each{it->
					if(new File(it).exists()){
						FileUtils.cleanDirectory(new File(it))
					}
				}
			}else{
				if(new File("${clean}").exists()){
					FileUtils.cleanDirectory(new File("${clean}"))
				}
			}
		}
		if(delete){
			if([Collection, Object[]].any { it.isAssignableFrom(delete.getClass()) }){
				delete.each{it->
					if(new File(it).exists()){
						new File(it).delete()
					}
				}
			}else{
				if(new File("${delete}").exists()){
					new File("${delete}").delete()
				}
			}
		}
	}
	def static execute(@DelegatesTo(FileBetch) Closure block){
		FileBetch m = new FileBetch()
		block.delegate = m
		block()
		m.executer()
	}
}