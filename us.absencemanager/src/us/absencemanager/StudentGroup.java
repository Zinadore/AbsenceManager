/**
 * 
 */
package us.absencemanager;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
/**
 * A proxy class to be serialized in place of a StudentGroup object.
 * @author Ioannis Boutsikas
 */
class StudentGroupProxy implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2137714849076341078L;
	Manager m = Manager.getInstance();
	private String name;
	private ArrayList<String> studentIDs;
	
	/**
	 * Creates a proxy object from a StudentGroup object.
	 * @param o The StudentGroup object to be converted.
	 */
	public StudentGroupProxy(StudentGroup o) {
		this.name = o.getName();
		ArrayList<Student> temp = o.getStudentList();
		
		for(Student s: temp){
			studentIDs.add(s.getId());
		}
	}
	
	/**
	 * Creates a StudentGroup object from a serialized StudentGroupProxy object.
	 * @return A StudentGroup object
	 * @throws ObjectStreamException
	 */
	private Object readResolve() throws ObjectStreamException {
		StudentGroup result = new StudentGroup(name);
		ArrayList<Student> temp = m.getStudents();
		for (String s: studentIDs){
			for(Student stud: temp){
				if(stud.getId().equals(s)) {
					result.addStudent(stud);
				}
			}
		}
		return result;
	}
	
}
/**
 * The StudentGroup represents a group of students. Has a name to be identified by and a collection of Students.
 * @author Ioannis Boutsikas
 *
 */
public class StudentGroup implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3436520038042407800L;
	private String name;
	private ArrayList<Student> students;
	
	/**
	 * COnstructs and initializes a StudentGroup with given name 
	 * @param name The name of the group
	 */
	public StudentGroup(String name) {
		setName(name);
		students = new ArrayList<Student>();
	}
	/**
	 * Serializes the object through a proxy, instead of directly.
	 * @return A StudentGroupProxy object.
	 * @throws java.io.ObjectStreamException
	 */
	private Object writeReplace() throws java.io.ObjectStreamException
    {
        return new StudentGroupProxy(this);
    }
	
	/**
	 * Adds a Student to the Student collection
	 * @param s The Student to add
	 */
	public void addStudent(Student s) {
		if(s != null) {
			students.add(s);
		}
	}
	/**
	 * Retrieves a Student by id, from the collection
	 * @param Id The id of the Student.
	 * @return The Student if found, null otherwise.
	 */
	public Student getStudent(String Id) {
		for (Student s: students) {
			if(s.getId() == Id) {
				return s;
			}
		}
		return null;
	}
	/**
	 * 
	 * @return The collection of Students
	 */
	public ArrayList<Student> getStudentList() {
		return this.students;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((students == null) ? 0 : students.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StudentGroup other = (StudentGroup) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (students == null) {
			if (other.students != null)
				return false;
		} else if (!students.equals(other.students))
			return false;
		return true;
	}
	
}
