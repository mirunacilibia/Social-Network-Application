package domain;

import java.util.*;

public class Group extends HashMap<List<User>, List<Message>> {
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Function verify if the HashMap contains a key
     * @param key List of users
     * @return true if already exist the list of users, or false otherwise
     */
    @Override
    public boolean containsKey(Object key) {
        List<User> list = ( List<User>) key ;
        for ( List<User> k : keySet() ) {
            if(list.size()==k.size())
                for(int i=0;i<k.size();i++){
                    if(!list.contains(k.get(i)))
                        break;
                    if(i==k.size()-1)
                        return true;
                }
        }
        return false;
    }

}
