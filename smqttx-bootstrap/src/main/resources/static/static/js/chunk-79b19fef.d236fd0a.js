(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-79b19fef"],{1402:function(t,e,a){},"3c11":function(t,e,a){},"67e8":function(t,e,a){},c4f9:function(t,e,a){"use strict";a("1402")},c8c3:function(t,e,a){"use strict";a.r(e);a("b0c0");var i=function(){var t=this,e=t._self._c;return e("page-layout",{attrs:{desc:t.desc,linkList:t.linkList}},[this.extraImage&&!t.isMobile?e("div",{staticClass:"extraImg",attrs:{slot:"extra"},slot:"extra"},[e("img",{attrs:{src:t.extraImage}})]):t._e(),e("page-toggle-transition",{attrs:{disabled:t.animate.disabled,animate:t.animate.name,direction:t.animate.direction}},[e("router-view",{ref:"page"})],1)],1)},s=[],n=a("5530"),r=function(){var t=this,e=t._self._c;return e("div",{staticClass:"page-layout"},[e("page-header",{ref:"pageHeader",style:"margin-top: ".concat(t.multiPage?0:-24,"px"),attrs:{breadcrumb:t.breadcrumb,title:t.pageTitle,logo:t.logo,avatar:t.avatar}},[t._t("action",null,{slot:"action"}),t._t("headerContent",null,{slot:"content"}),!this.$slots.headerContent&&t.desc?e("div",{attrs:{slot:"content"},slot:"content"},[e("p",[t._v(t._s(t.desc))]),this.linkList?e("div",{staticClass:"link"},[t._l(t.linkList,(function(a,i){return[e("a",{key:i,attrs:{href:a.href}},[e("a-icon",{attrs:{type:a.icon}}),t._v(t._s(a.title))],1)]}))],2):t._e()]):t._e(),this.$slots.extra?t._t("extra",null,{slot:"extra"}):t._e()],2),e("div",{ref:"page",class:["page-content",t.layout,t.pageWidth]},[t._t("default")],2)],1)},c=[],o=(a("d3b7"),a("159b"),a("14d9"),a("4de4"),a("caad"),a("2532"),function(){var t=this,e=t._self._c;return e("div",{class:["page-header",t.layout,t.pageWidth]},[e("div",{staticClass:"page-header-wide"},[e("div",{staticClass:"breadcrumb"},[e("a-breadcrumb",t._l(t.breadcrumb,(function(a,i){return e("a-breadcrumb-item",{key:i},[e("span",[t._v(t._s(a))])])})),1)],1),e("div",{staticClass:"detail"},[e("div",{staticClass:"main"},[e("div",{staticClass:"row"},[t.showPageTitle&&t.title?e("h1",{staticClass:"title"},[t._v(t._s(t.title))]):t._e(),e("div",{staticClass:"action"},[t._t("action")],2)]),e("div",{staticClass:"row"},[this.$slots.content?e("div",{staticClass:"content"},[t.avatar?e("div",{staticClass:"avatar"},[e("a-avatar",{attrs:{src:t.avatar,size:72}})],1):t._e(),t._t("content")],2):t._e(),this.$slots.extra?e("div",{staticClass:"extra"},[t._t("extra")],2):t._e()])])])])])}),u=[],l=a("5880"),g={name:"PageHeader",props:{title:{type:[String,Boolean],required:!1},breadcrumb:{type:Array,required:!1},logo:{type:String,required:!1},avatar:{type:String,required:!1}},computed:Object(n["a"])({},Object(l["mapState"])("setting",["layout","showPageTitle","pageWidth"]))},d=g,h=(a("d84b"),a("2877")),p=Object(h["a"])(d,o,u,!1,null,"40581fd6",null),f=p.exports,m=a("89a5"),b={name:"PageLayout",components:{PageHeader:f},props:["desc","logo","title","avatar","linkList","extraImage"],data:function(){return{page:{},pageHeaderHeight:0}},watch:{$route:function(){this.page=this.$route.meta.page}},updated:function(){this._inactive||this.updatePageHeight()},activated:function(){this.updatePageHeight()},deactivated:function(){this.updatePageHeight(0)},mounted:function(){this.updatePageHeight()},created:function(){this.page=this.$route.meta.page},beforeDestroy:function(){this.updatePageHeight(0)},computed:Object(n["a"])(Object(n["a"])({},Object(l["mapState"])("setting",["layout","multiPage","pageMinHeight","pageWidth","customTitles"])),{},{pageTitle:function(){var t=this.page&&this.page.title;return this.customTitle||t&&this.$t(t)||this.title||this.routeName},routeName:function(){var t=this.$route;return this.$t(Object(m["b"])(t.matched[t.matched.length-1].path))},breadcrumb:function(){var t=this,e=this.page,a=e&&e.breadcrumb;if(a){var i=[];return a.forEach((function(e){i.push(t.$t(e))})),i}return this.getRouteBreadcrumb()},marginCorrect:function(){return this.multiPage?24:0}}),methods:Object(n["a"])(Object(n["a"])({},Object(l["mapMutations"])("setting",["correctPageMinHeight"])),{},{getRouteBreadcrumb:function(){var t=this,e=this.$route.matched,a=this.$route.path,i=[];e.filter((function(t){return a.includes(t.path)})).forEach((function(e){var a=0===e.path.length?"/home":e.path;i.push(t.$t(Object(m["b"])(a)))}));var s=this.page&&this.page.title;return(this.customTitle||s)&&(i[i.length-1]=this.customTitle||s),i},updatePageHeight:function(){var t=arguments.length>0&&void 0!==arguments[0]?arguments[0]:this.$refs.pageHeader.$el.offsetHeight+this.marginCorrect;this.correctPageMinHeight(this.pageHeaderHeight-t),this.pageHeaderHeight=t}})},v=b,_=(a("c4f9"),Object(h["a"])(v,r,c,!1,null,null,null)),H=_.exports,P=a("7664"),x={name:"PageView",components:{PageToggleTransition:P["a"],PageLayout:H},data:function(){return{page:{}}},computed:Object(n["a"])(Object(n["a"])({},Object(l["mapState"])("setting",["isMobile","multiPage","animate"])),{},{desc:function(){return this.page.desc},linkList:function(){return this.page.linkList},extraImage:function(){return this.page.extraImage}}),mounted:function(){this.page=this.$refs.page},updated:function(){this.page=this.$refs.page}},y=x,C=(a("feba"),Object(h["a"])(y,i,s,!1,null,"38fc72ea",null));e["default"]=C.exports},d84b:function(t,e,a){"use strict";a("67e8")},feba:function(t,e,a){"use strict";a("3c11")}}]);