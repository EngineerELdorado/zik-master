<?php 
session_start();
require_once('../includes/config.php');
$db = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
mysqli_set_charset( $db, 'utf8');
if(isset($_REQUEST['useremail']) && $_REQUEST['password']) :
$useroremail = $_REQUEST['useremail'];	
$pass = md5($_REQUEST['password']);

    
    $query = mysqli_query($db,"select * from users where username='$useroremail' && password='$pass' || email='$useroremail' && password='$pass'");
     if(mysqli_num_rows($query) >0) :
      while($row = mysqli_fetch_object($query)) :
       $time = time();
      $session = '0';
      $db->query("UPDATE users set online = '$time', logout_time = '$session' where idu = '$row->idu'");
       $inf['userlogin'][] = $row;
       $inf['success']=1;
       endwhile;
      
       else :
       $inf['userlogin']='Invalid User';
       $inf['success']=0;
      
     endif;
 $val = json_encode($inf);
       echo $val; 

	endif;
?>