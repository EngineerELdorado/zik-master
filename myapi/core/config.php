<?php 
 unset($_REQUEST['lang'],$_REQUEST['cpsession'],$_REQUEST['PHPSESSID'],$_REQUEST['request']);
class config
{
	
	    
	     protected $db;
	   
         function __construct()
		 {
		   include_once "../includes/config.php";
		   
		   $this->db = new mysqli($CONF['host'],$CONF['user'],$CONF['pass'],$CONF['name']);	 
		   mysqli_set_charset($this->db, 'utf8');
		   	 
		 }
	
	    function connection()
		{
		   
			if($this->db) 
			{
				echo "connection done";
			}
			else 
			{
			   echo "connect fail";	
			}
		 	
		}
	
	    
	    function registerDevice()
		{
			
			$date = date('Y-m-d');
			$data = array_merge($_REQUEST,array('created_at'=>$date));
			$keys = implode(",",array_keys($data));
			$q=$this->db->query("insert into device_register($keys) value('".implode("','",$data)."')");
			if($q) :
			$set['token'][] = array('message'=>'device register');
			$set['status'] = 'success';
			else : 
			$set['token'][] = array('message'=>'device not register');
			$set['status'] = 'fail';
			endif;
			echo json_encode($set);
		}
	
	
	
	    function byAction()
		{
			$type   = $_REQUEST['type'];
			unset($_REQUEST['type']);
			$data   = $_REQUEST;
			$keys   = implode(",$type.",array_keys($data));
			$types  = $this->types($type);
			
			if(array_key_exists('by',$data)) : 
			$by      =   $data['by']; 
			$sender  =   $this->from($by);
			$trackid =  $data['track'];
			$track   =  $this->tracks($trackid);
			$to      =  $track['idu'];
			$this->likes_track($trackid);
			$message =  "$sender liked your track";
			else : 
			$by      =  $data['uid']; 
			$sender  =  $this->from($by);
			$trackid =  $data['tid'];
			$track   =  $this->tracks($trackid);
			$to      =  $track['idu'];
			$message =  "$sender commented on your track";
			$comment =  $this->db->real_escape_string(htmlspecialchars($_REQUEST['message']));
			$data    =  $_REQUEST;
			endif;
			$query   =  $this->db->query("insert into $type($type.$keys) value('".implode("','",$data)."')");
			
			$token   =  $this->check($track['idu'],$types);
			$message =  array('title'=>$track['title'],"body"=>$message);
			if($token[1]!=$by) {
			if($token['switch']=='on') :
			unset($token['switch']);
			$this->FCM($token,$message);
			     $set[$type][] = array('message'=>"success");
			    $set['success'] =1;
			else :
			    $set[$type][] = $token['message'];
			    $set['success'] =1;
			endif;
			}
			else 
			{
			 	$set[$type][] = array('message'=>"success");
			    $set['success'] =1;
			}
            $this->notification($by,$to,$track['id'],$types);
			echo json_encode($set);
		}
	
	    function check($idu,$type)
		{
			switch($type) 
			{
				case '1':
					$query = $this->db->query("select * from users where idu =".$idu);
					$row = $query->fetch_object();
					if($row->notificationc!=0) :
					$token = $this->tokens($idu);
					return array($token['token'],$token['userid'],'switch'=>'on');
					else : 
					return array("message"=>'success notification off','swicth'=>'off');
					endif;
					break;
				case '2' :
					$query = $this->db->query("select * from users where idu =".$idu);
					$row = $query->fetch_object();
					if($row->notificationl!=0) :
					$token = $this->tokens($idu);
					return array($token['token'],$token['userid'],'switch'=>'on');
					else : 
					return array("message"=>'success notification off','swicth'=>'off');
					endif;
					break;
				case '3' :
					$query = $this->db->query("select * from users where idu =".$idu);
					$row = $query->fetch_object();
					if($row->notificationd!=0) :
					$token = $this->tokens($idu);
					return array($token['token'],$token['userid'],'switch'=>'on');
					else : 
					return array("message"=>'success notification off','swicth'=>'off');
					endif;
					break;
				case '4' :
					$query = $this->db->query("select * from users where idu =".$idu);
					$row = $query->fetch_object();
					if($row->notificationf!=0) :
					$token = $this->tokens($idu);
					return array($token['token'],$token['userid'],'switch'=>'on');
					else : 
					return array("message"=>'success notification off','swicth'=>'off');
					endif;
					break;	
				default : 
					echo "no matched";
					
			}
		}
	
	    
	   function FCM($registratoin_ids,$data)
		{
		  define("GOOGLE_API_KEY","AAAAk40bNiE:APA91bGOvWosLbIXNH2MkEbDhYgZKhBZHFDgzywNY2xhcNyumTm9VRUGE5BUslru5OgPHE4ZBz1x0ye1I1VJmqv4uIcjijZrLZHN67vbdXDriEMGl2sRIcj369tItnWvnjScDQGKU8Oy");	
			$url = "https://fcm.googleapis.com/fcm/send";
			
			$fields = array (
                "registration_ids" => $registratoin_ids,
                'notification' =>$data
            );
			
			$headers = array(
                "Authorization: key=".GOOGLE_API_KEY,
                "Content-Type: application/json"
            );
			$ch = curl_init();
            curl_setopt($ch, CURLOPT_URL, $url);
            curl_setopt($ch, CURLOPT_POST, true);
            curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
            curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
            curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
            $result_fcm=curl_exec($ch);
			($result_fcm);
			if($result_gcm===FALSE) {
                die("Curl failed: ".curl_error($ch));
            }
            curl_close($ch);
			
		}
	
	    function SendMail($email,$name,$user,$ids,$title)
		{
				   $to = $email;
				   $subject = $name.  ' liked your track';
				   $message .= 'Hello  '.$user."<br>";
				   $message .= '<a href="http://singering.com/index.php?a=profile&u='.$name.'">'.$name.'</a> liked your <a href="http://singering.com/index.php?a=track&id='.$ids.'&name='.urlencode($title).'">track</a>';
				   $header = "From: no-reply@singering.com". "\r\n";
				   $header .= "MIME-Version: 1.0". "\r\n";
				   $header .= "Content-type:text/html;charset=UTF-8" . "\r\n";
		}
	
	    
	    function tokens($id)
		{
		
			$query = $this->db->query("select * from device_register where user_id =".$id);	
			if($query->num_rows>0)
			{
				while($row = $query->fetch_assoc())
				{
					return array("token"=>$row['device_token'],'userid'=>$row['user_id']);
				}
			}
		}
	
	   function from($id)
	   {
		  $query = $this->db->query("select * from users where idu =".$id);
		   $row  = $query->fetch_assoc();
		   return $row['username'];
	   }
	    
	    function likes_track($trackid)
		{
		     $likes = 1;
			 $query = $this->db->query("select * from tracks where tracks.id = $trackid");
			 if($query) :
			 $row   = $query->fetch_assoc();
			 $like  = $row['likes'] + $likes;
			 $this->db->query("update tracks set likes = '$like' where tracks.id = $trackid");
			 endif;
			  
		}
	    function tk()
		{
		      $this->likes_track(47);
		}
	   function tracks($id)
	   {
		   $q = $this->db->query("select * from `tracks`,`users` where `users`.`idu`=`tracks`.`uid` AND `tracks`.`id` =".$id);   
		   $row = $q->fetch_assoc();
		   return $rw[] = $row;
	   }
	
	  function notification($by,$to,$track,$type)
	  {
		  $date = date('Y-m-d h:i:s');
		  $this->db->query("insert into notifications(notifications.from,notifications.to,notifications.parent,notifications.type) value('$by','$to','$track','$type')");
	  }
	
	 function types($type)
	 {
		       
		 switch ($type)
		 {
			 case 'comments' :
		     return 1;
		     break;
			 case 'likes' :
			 return  2;
			 case 'chat' :
			 return 3;	 
			 break;
			 default : 
				 return 4;			 
		 }
	 }
	
	 function chats()
	 {
		 $type = $_REQUEST['type'];
		 unset($_REQUEST['type']);
		 $chtm  = $this->db->real_escape_string(htmlspecialchars($_REQUEST['message']));
		 $data = array_merge($_REQUEST,array('message'=>$chtm));
		 $types  = $this->types($type);
		 $keys = implode(",$type.",array_keys($data));
		 $by     = $data['from']; 
		 $sender = $this->from($by);
		 $to     = $this->from($data['to']);
		 $query =$this->db->query("insert into $type($type.$keys) value('".implode("','",$data)."')");
		 $message = array('title'=>"singering","body"=>"$sender sent you a chat message.");
		 $token = $this->check($data['to'],$types);
			if($token['switch']=='on') :
			unset($token['switch']);
			$this->FCM($token,$message);
			     $set[$type][] = array('message'=>"success");
			    $set['success'] =1;
			else :
			    $set[$type][] = $token['message'];
			    $set['success'] =1;
			endif;
			echo json_encode($set);
	 }
	
	 function uploadtrack()
	 {
		 $tb    = "tracks";
		 $id    = $_REQUEST['uid'];
		 $name  = $this->from($id);
		 $image = $this->images($_REQUEST['image']);
		 
		  $path  = "../uploads/tracks/";
		  $upload = $_FILES['uploadtrack']['name'];
		  $trackupload  = $path.basename($_FILES['uploadtrack']['name']);
		  move_uploaded_file($_FILES['uploadtrack']['tmp_name'], $trackupload);
		  $size = $_FILES['uploadtrack']['size'];
		  $data  = array('title'=>$this->db->real_escape_string(htmlspecialchars($_REQUEST['title'])),'uid'=>$_REQUEST['uid'],'description'=>$this->db->real_escape_string(htmlspecialchars($_REQUEST['description'])),'name'=>$upload,'art'=>$image,'tag'=>$this->db->real_escape_string(htmlspecialchars($_REQUEST['tag'])),'size'=>$size,'public'=>1,'time'=>date('Y-m-d h:i:s'));
		 $keys  = implode(",$tb.",array_keys($data));
		 //print_r("insert into $tb($tb.$keys) value('".implode("','",$data)."')");
		 $trk = $this->db->query("insert into $tb($tb.$keys) value('".implode("','",$data)."')");
		 if($trk) :
		 $q   = $this->db->query("select * from relations where subscriber = '$id'"); 
		 while($row = $q->fetch_assoc())
		      {
			        $leader[] = $row['leader']; 
		      }
		foreach($leader as $ids)
		{
			$query = $this->db->query("select * from device_register where user_id = '$ids'");
			$row   = $query->fetch_object();
			$tokens[] = $row->device_token;
		}
		 
		 $m = array("title"=>"singering","body"=>"$name Uploaded New Track");
		 $this->FCM($tokens,$m);
		    $set['upload'][] = array("message"=>"successs");
		    $set['success'] = 1;
		 else :
		    $set['upload'][] = array("message"=>"fail");
		    $set['success'] = 0;
		 endif;
		 echo json_encode($set);
	 }
	 
	
	 function images($image)
			{      
				 if(!empty($image)) :
                      $base=$image;
						$binary=base64_decode($base);
						header('Content-Type: bitmap; charset=utf-8');
						$time=time();
						$imgt=$time.'.jpg';
						$file = fopen("../uploads/media/".$imgt, 'wb');
						$img=fwrite($file,$binary);
				       return $imgt;
						fclose($file); 
				else :
				         return 'default.png';
				endif;
			}
	
	   
		function addtofriend()
		{
            $table  = 'relations';
			$data   = array('leader'=>$_REQUEST['friendid'],'subscriber'=>$_REQUEST['userid']);
			$types  = $this->types($table);
			$from   =  $this->from($data['subscriber']);
			$this->db->query("insert into relations(relations.leader,relations.subscriber) value('".implode("','",$data)."')");
			
			$token  = $this->check($data['leader'],$types);
			$message = array('title'=>'singering',"body"=>"$from added as friend");
			$this->notification($data['subscriber'],$data['leader'],0,$types);
		    if($token['switch']=='on') :
			unset($token['switch']);
			$this->FCM($token,$message);
			     $set[$table][] = array('message'=>"success");
			     $set['success'] =1;
			else :
			    $set[$table][] = $token['message'];
			    $set['success'] =1;
			endif;
			echo json_encode($set);
		}
	
	    function tokenreset()
		{   
			$token = $_REQUEST['token'];
			$id    = $_REQUEST['uid'];
			$query = $this->db->query("update `device_register` set `device_token`='$token' where user_id =$id ");
			if($query) :
			   $set['token'][]  = array("message"=>'refresh token');
			   $set['success'] = 1;
			else : 
			 $set['token'][]  = array("message"=>'token not refresh');
			   $set['success'] = 0;
			endif;
			echo json_encode($set);
		}
}
?>