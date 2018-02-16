<?php 
require_once('../includes/config.php');
$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
mysqli_set_charset( $conn, 'utf8');
if(isset($_REQUEST['profilemodify']) && $_REQUEST['profilemodify']=='true' && $_REQUEST['u_ids']!=null):
$id = $_REQUEST['u_ids'];
$fname=  htmlspecialchars($_REQUEST['fname']);
$lname = htmlspecialchars($_REQUEST['lname']);
$email = htmlspecialchars($_REQUEST['email']);
$count = htmlspecialchars($_REQUEST['country']);
$city=   htmlspecialchars($_REQUEST['city']);
$web=    htmlspecialchars($_REQUEST['website']);
$pro=    htmlspecialchars($_REQUEST['profile']);
$desc=   htmlspecialchars($_REQUEST['description']);
$offline=   htmlspecialchars($_REQUEST['offline']);
$query = $conn->query("update users set email = '$email',first_name='$fname',last_name='$lname',country = '$count',city='$city',website='$web',description='$desc',private='$pro' ,offline='$offline' where idu= '$id'");
if($query) :
$set['profile-up'][]=array('message'=>'update success');
$set['success']=1;
else :
$set['profile-up'][]=array('message'=>'update fail');
$set['success']=0;
endif;
$val=json_encode($set);
echo $val;
elseif(isset($_REQUEST['profileimage']) && $_REQUEST['profileimage']=='true' && $_REQUEST['user_id']!=null && $_REQUEST['userpic']!=null) :
     $id = $_REQUEST['user_id'];
     if($_REQUEST['userpic']==null) :
     	 $set['userimage'][]='Please Select Image';
     	 $set['success']=0;
     	endif;
     $base=$_REQUEST['userpic'];
	 
     $query = $conn->query("select * from users where idu = '$id'");
     $num = $query->num_rows;
       if($num > 0) :
         $row = $query->fetch_object();
         if($row->image!='default.png') : 
               unlink('../uploads/avatars/'.$row->image);
         	endif;
         	$idu=$row->idu;
        $binary=base64_decode($base);
		header('Content-Type: bitmap; charset=utf-8');
		$time=time();
		$imgt=$time.'.jpg';
		$file = fopen("../uploads/avatars/".$imgt, 'wb');
		$img=fwrite($file,$binary);
		fclose($file);	
         $up= $conn->query("update users set image='$imgt' where idu = '$idu' ");
         $set['userimage']=$imgt;
         $set['success']=1;
      else :
      $set['userimage'][] = 'invalid users';
      $set['success']=0;
    endif;  
    $val=json_encode($set);
    echo $val;
    elseif(isset($_REQUEST['passwordchange']) && $_REQUEST['passwordchange']=='yes' && $_REQUEST['us_id']!='') :

        $id = $_REQUEST['us_id'];
        $currentpass  = md5($_REQUEST['currentpass']);

        $newpass  = md5($_REQUEST['newpass']);

        $query = $conn->query("select * from users where idu = '$id' && password = '$currentpass'");
        $num = $query->num_rows;
        if($num >0) :
                 
            $q = $conn->query("update users set password = '$newpass' where idu = '$id'");
            if($q) :
             $set['newpass'][] = array('message'=>'Password changed');
             $set['success']=1;
            else :
                  $set['newpass'][] = array('message '=>'Request Fail');
                  $set['success']=0;
            endif;

          else :
                  $set['newpass'][] = array('message'=>'Password Not Match');
                  $set['success']=0;

      endif;
      $val = json_encode($set);
      echo $val;
     
    elseif(isset($_REQUEST['coverwall']) && $_REQUEST['coverwall']=='true' && $_REQUEST['user_id']!=null &&  $_REQUEST['coverpic']!=null) :
      $id = $_REQUEST['user_id'];
     if($_REQUEST['coverpic']==null) :
     	 $set['userimage'][]='Please Select Image';
     	 $set['success']=0;
     	endif;
     $base=$_REQUEST['coverpic'];
	 
     $query = $conn->query("select * from users where idu = '$id'");
     $num = $query->num_rows;
       if($num > 0) :
         $row = $query->fetch_object();
         if($row->image!='default.png') : 
               unlink('../uploads/covers/'.$row->image);
         	endif;
         	$idu=$row->idu;
        $binary=base64_decode($base);
		header('Content-Type: bitmap; charset=utf-8');
		$time=time();
		$imgt=$time.'.png';
		$file = fopen("../uploads/covers/".$imgt, 'wb');
		$img=fwrite($file,$binary);
		fclose($file);	
         $up= $conn->query("update users set cover='$imgt' where idu = '$idu' ");
         $set['userimage']=$imgt;
         $set['success']=1;
      else :
      $set['userimage'][] = 'invalid users';
      $set['success']=0;
    endif;  
    $val=json_encode($set);
    echo $val;

     elseif(isset($_REQUEST['social']) && $_REQUEST['social']=='true' && $_REQUEST['urid']!='') :
        
            $uid = $_REQUEST['urid'];
            $fb =  $_REQUEST['fb'];
            $tw =  $_REQUEST['twitter'];
            $gplus = $_REQUEST['gplus'];
            $youtube = $_REQUEST['youtube'];
            $vimeo = $_REQUEST['vimeo'];
            $tumblr = $_REQUEST['tumblr'];
            $soundclud = $_REQUEST['soundcloud'];
            $myspace = $_REQUEST['myspace'];
            $lastfm = $_REQUEST['lastfm'];

            $query = $conn->query("update users set facebook = '$fb',twitter = '$tw',gplus = '$gplus', youtube ='$youtube', vimeo ='$vimeo',tumblr ='$tumblr', soundcloud = '$soundclud',myspace ='$myspace',lastfm = '$lastfm'  where idu = '$uid'");

            if($query) :
                   
                   $set['social'][] = array('message'=>'update social');
                   $set['success']=1;

              else :

                    $set['social'][] = array('message'=>'update fail');
                    $set['success']=0;

                endif;

                 $val = json_encode($set);
                 echo $val;
 
  
    else :
    	$set['Request Denied'][] = 'Go Away';
     $val=json_encode($set);
     echo $val;
	endif;


?>