<?php 
require_once('../includes/config.php');
$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);

       
       $query = $conn->query("select users.idu,users.username,users.first_name,users.last_name,users.country,users.city,users.image from users ORDER BY users.idu DESC limit 10");

       if($query) :
            while($row = $query->fetch_object()) :
                   $set['users'][] = $row;
                   $set['success'] = 1;

            	endwhile;
          else :
               $set['users'] = 'no recent users';
               $set['success']=0;
       	endif;
       	$val = json_encode($set,JSON_HEX_AMP);
       	echo $val; 
	
?>