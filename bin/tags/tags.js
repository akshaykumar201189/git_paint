riot.tag2('gp-draw', '<canvas id="drawer"></canvas><button class="save"><svg xmlns="http://www.w3.org/2000/svg" fill="#FFFFFF" height="24" viewbox="0 0 24 24" width="24"><path d="M0 0h24v24H0z" fill="none"></path><path d="M17 3H5c-1.11 0-2 .9-2 2v14c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V7l-4-4zm-5 16c-1.66 0-3-1.34-3-3s1.34-3 3-3 3 1.34 3 3-1.34 3-3 3zm3-10H5V5h10v4z"></path></svg></button>', '', '', function(opts) {
	var curr_img=null;
	this.on("mount",function(){
		document.querySelector('body').style.overflow="hidden";
		var el = document.getElementById('drawer');
		el.setAttribute("width",window.innerWidth);
		el.setAttribute("height",window.innerHeight);
		var ctx = el.getContext('2d');
		var isDrawing;

		el.addEventListener("touchstart", function(e) {
		  isDrawing = true;
		  ctx.lineWidth = 10;
		  ctx.lineJoin = ctx.lineCap = 'round';
		  ctx.shadowBlur = 10;
		  ctx.shadowColor = 'rgb(0, 0, 0)';
		  ctx.moveTo(e.touches[0].clientX, e.touches[0].clientY-58);
		});
		el.addEventListener("touchmove", function(e) {
		  if (isDrawing) {
		    ctx.lineTo(e.touches[0].clientX, e.touches[0].clientY-58);
		    ctx.stroke();
		  }
		});
		el.addEventListener("touchend", function(e) {
		  isDrawing = false;
		});

		document.querySelector(".save").addEventListener("click",function(e){
			fetch('http://172.20.41.158:8080/galleria/save',{
				headers: {
			      'Accept': 'application/json',
			      'Content-Type': 'application/json'
		    	},
			    method: "POST",
			    body: JSON.stringify({
			    	"image_id":curr_img,
			    	"title":"img_"+Date.now(),
			    	"image":el.toDataURL(),
			    })
			}).then(res=>res.json()).then(data=>{
				curr_img=data.image_id;
			});
		})
	});

	this.on("unmount",function(){
		document.querySelector('body').style.overflow="auto";
	})
});
riot.tag2('gp-home', '<gp-userpic></gp-userpic><gp-userpic></gp-userpic><gp-userpic></gp-userpic><gp-userpic></gp-userpic><gp-userpic></gp-userpic><gp-userpic></gp-userpic><gp-userpic></gp-userpic>', '', '', function(opts) {
});
riot.tag2('gp-loader', '<div class="loader"><svg class="circular" viewbox="25 25 50 50"><circle class="path" cx="50" cy="50" r="20" fill="none" stroke-width="2" stroke-miterlimit="10"></circle></svg></div>', '', '', function(opts) {
});
riot.tag2('gp-profile', '<div class="loader {profileData ? \'hidden\' : \'\'}"><gp-loader></gp-loader></div><div class="profile {!profileData ? \'hidden\' : \'\'}"><div class="userinfo"><div class="pic"><img class="userpic" riot-src="{profileData.user.avatar_url}"></img></div><div class="info"><div class="name">{profileData.user.name}</div><div class="desc">front end engineer @ nowhere</div><div class="container-followers"><a class="followers" href="/followers/prateek">22 followers</a></div></div></div><div class="tabs"><div class="tab own selected" onclick="{selTab}"><div class="num">{profileData.owned_images&&profileData.owned_images.length}</div><div class="label">OWN</div></div><div class="tab cloned" onclick="{selTab}"><div class="num">{profileData.cloned_images&&profileData.cloned_images.length}</div><div class="label">CLONED</div></div><div class="tab pr" if="{profileData.user.account_id===window.uid}" onclick="{selTab}"><div class="num">{profileData.pull_requests&&profileData.pull_requests.length}</div><div class="label">PRs</div></div></div><div class="content"><div if="{selectedTab===0}"><a class="imagelink" each="{image in profileData.owned_images}"><img class="imgthumb" riot-src="{window.ip+\'/image/\'+ image.image_id +\'/data\'}"></img></a></div><div if="{selectedTab===1}"><a class="imagelink" each="{image in profileData.cloned_images}"><img class="imgthumb" riot-src="{window.ip+\'/image/\'+ image.image_id +\'/data\'}"></img></a></div><div if="{selectedTab===2}"><a class="pr" each="{pr in profileData.pull_requests}" href="/pr/1"><img class="sender" riot-src="{pr.sender.avatar_url}"></img><span class="info"><b>{pr.sender.name}</b> sent a pr on</span><img class="source" riot-src="{window.ip + ⁗/image/⁗+pr.image.image_id+⁗/data⁗}"></img></a></div></div></div>', '', '', function(opts) {
		var self=this;
		this.selectedTab=0;
		this.on("mount",function(e){
			fetch(window.ip+"/galleria/profile/details/"+veronica.getCurrentState().data[":user"],{
				headers: { 'x-account-id': window.uid }
			})
			.then(res=>res.json())
			.then(data=>{
				self.update({profileData: data});
			})
		})

		this.selTab=function(e){
			document.querySelector(".selected")&&document.querySelector(".selected").classList.remove("selected");
			e.target.classList.add("selected");
			if(e.target.classList.contains("cloned")){
				self.update({selectedTab: 1});
			} else if(e.target.classList.contains("own")){
				self.update({selectedTab: 0});
			} else {
				self.update({selectedTab: 2});
			}
		}
});
riot.tag2('gp-search', '<input type="text" class="q" placeholder="explore"><div class="loader {!resultShown ? \'\' : \'hidden\'}"><gp-loader></gp-loader></div><div class="results {!resultShown ? \'hidden\' : \'\'}"><div class="ppl"><div class="title">People</div><div class="content" each="{user in searchdata.users}"><a href="/user/{user.account_id}" class="usernail"><div class="pic"><img class="pic" riot-src="{user.avatar_url}"></img></div><div class="name">{user.name}</div></a></div></div><div class="tags"><div class="title">Tags</div><div class="content"><a class="tag" href="/tags/{tag}" each="{tag in searchdata.tags}">#{tag}</a></div></div></div>', '', '', function(opts) {
		var self=this;

		this.searchdata={};
		this.resultShown=false;

		this.on("mount",function(){
			document.querySelector(".q").addEventListener("keyup",function(e){
				document.querySelector(".results").classList.add("hidden");
				document.querySelector(".loader").classList.remove("hidden");
				self.update({ resultShown: false });
				fetch(window.ip+"/galleria/search/any/"+document.querySelector(".q").value)
				.then(res=>res.json()).then(data=>{
					self.update({ searchdata: data, resultShown: true });
					document.querySelector(".results").classList.remove("hidden");
					document.querySelector(".loader").classList.add("hidden");
				}).catch(()=>{
					self.update({ resultShown: false });
				});
			});
		})

});
riot.tag2('gp-tagpics', '<div class="content"></div>', '', '', function(opts) {
		this.on("mount",function(){
			document.querySelector("h1").innerHTML="tag: "+veronica.getCurrentState().data[":tag"];
		});
		this.on("unmount",function(){
			document.querySelector("h1").innerHTML="Painteria";
		});
});
riot.tag2('gp-userpic', '<div class="userinfo"><div class="piccontainer"><img class="userpic" height="50" width="50" src="https://igcdn-photos-a-a.akamaihd.net/hphotos-ak-xat1/t51.2885-19/s150x150/12912528_235034833513880_374146734_a.jpg"></img></div><a href="/user/prateek" class="username">Prateek Bhatnagar</a><span class="time">15m</span></div><div class="painting" onclick="{toggleCopy}"><img class="img-painting" src="https://igcdn-photos-e-a.akamaihd.net/hphotos-ak-xtp1/t51.2885-15/e35/13712640_1582972958669636_496558116_n.jpg"><button class="copy hidden"><svg xmlns="http://www.w3.org/2000/svg" fill="#FFFFFF" height="24" viewbox="0 0 24 24" width="24"><path d="M0 0h24v24H0z" fill="none"></path><path d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z"></path></svg></button></div>', '', '', function(opts) {
		var showingCopy=false;
		var $copy=this.root.querySelector(".copy");
		this.on("mount",function(e){
			$copy=this.root.querySelector(".copy");
		});
		this.toggleCopy=function(){
			if(!showingCopy){
				$copy.classList.remove("hidden");
			}
			else{
				$copy.classList.add("hidden");
			}
			showingCopy=!showingCopy;
		}
});
riot.tag2('gp-header', '<header><svg class="draw" xmlns="http://www.w3.org/2000/svg" fill="#FFFFFF" height="24" viewbox="0 0 24 24" width="24"><path d="M0 0h24v24H0z" fill="none"></path><path d="M7 14c-1.66 0-3 1.34-3 3 0 1.31-1.16 2-2 2 .92 1.22 2.49 2 4 2 2.21 0 4-1.79 4-4 0-1.66-1.34-3-3-3zm13.71-9.37l-1.34-1.34c-.39-.39-1.02-.39-1.41 0L9 12.25 11.75 15l8.96-8.96c.39-.39.39-1.02 0-1.41z"></path></svg><div class="nav"><div class="icon"><svg xmlns="http://www.w3.org/2000/svg" fill="#FFFFFF" height="48" viewbox="0 0 24 24" width="48"><path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"></path><path d="M0 0h24v24H0z" fill="none"></path></svg></div><div class="icon"><svg xmlns="http://www.w3.org/2000/svg" fill="#FFFFFF" height="48" viewbox="0 0 24 24" width="48"><path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"></path><path d="M0 0h24v24H0z" fill="none"></path></svg></div><div class="icon"><svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" fill="#FFFFFF" height="48" viewbox="0 0 24 24" width="48"><defs><path d="M0 0h24v24H0V0z" id="a"></path></defs><clippath id="b"><use overflow="visible" xlink:href="#a"></use></clipPath><path clip-path="url(#b)" d="M20 4H4c-1.11 0-1.99.89-1.99 2L2 18c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V6c0-1.11-.89-2-2-2zM8.5 15H7.3l-2.55-3.5V15H3.5V9h1.25l2.5 3.5V9H8.5v6zm5-4.74H11v1.12h2.5v1.26H11v1.11h2.5V15h-4V9h4v1.26zm7 3.74c0 .55-.45 1-1 1h-4c-.55 0-1-.45-1-1V9h1.25v4.51h1.13V9.99h1.25v3.51h1.12V9h1.25v5z"></path></svg></div><div class="icon"><svg xmlns="http://www.w3.org/2000/svg" fill="#FFFFFF" height="48" viewbox="0 0 24 24" width="48"><path d="M12 5.9c1.16 0 2.1.94 2.1 2.1s-.94 2.1-2.1 2.1S9.9 9.16 9.9 8s.94-2.1 2.1-2.1m0 9c2.97 0 6.1 1.46 6.1 2.1v1.1H5.9V17c0-.64 3.13-2.1 6.1-2.1M12 4C9.79 4 8 5.79 8 8s1.79 4 4 4 4-1.79 4-4-1.79-4-4-4zm0 9c-2.67 0-8 1.34-8 4v3h16v-3c0-2.66-5.33-4-8-4z"></path><path d="M0 0h24v24H0z" fill="none"></path></svg></div><div class="icon"><svg xmlns="http://www.w3.org/2000/svg" fill="#FFFFFF" height="48" viewbox="0 0 24 24" width="48"><path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"></path><path d="M0 0h24v24H0z" fill="none"></path></svg></div></div><button class="menu"><svg fill="#FFFFFF" height="24" viewbox="0 0 24 24" width="24"><path d="M0 0h24v24H0z" fill="none"></path><path d="M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z"></path></svg></button><h1>Painteria</h1></header>', '', '', function(opts) {
	this.on("mount",function(){
		document.querySelector('.draw').addEventListener("click",function(){
			if(veronica.loc().name!=="draw")
				veronica.loc("/draw");
		});
	})

});