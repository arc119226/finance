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
					FileUtils.cleanDirectory(new File(it))
				}
			}else{
				FileUtils.cleanDirectory(new File("${clean}"))
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