package com.netease.edu.kada.cache.web;

import com.netease.edu.kada.cache.core.config.CacheWebProperties;
import com.netease.edu.kada.cache.core.dto.PageInfo;
import com.netease.edu.kada.cache.core.storage.CacheStorage;
import com.netease.edu.kada.cache.core.storage.ProjectCacheInvoke;
import com.netease.edu.kada.cache.core.storage.SearchParam;
import com.netease.edu.kada.cache.core.vo.ClassCacheVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * @author zhangchanglu
 * @since 2018/10/12 19:04.
 */
@Controller
public class CacheManagerController {
    @Resource
    private CacheStorage cacheStorage;
    @Resource
    private ProjectCacheInvoke projectCacheInvoke;
    @Resource
    private CacheWebProperties cacheWebProperties;

    @RequestMapping("/com/netease/edu/kada/cache/core/manager/all")
    @ResponseBody
    public PageInfo<ClassCacheVo> allCache(String appName, int pageIndex, int pageSize) {
        return cacheStorage.getAllCache(appName, pageIndex, pageSize);
    }

    @RequestMapping("/com/netease/edu/kada/cache/core/manager/search")
    @ResponseBody
    public PageInfo<ClassCacheVo> search(SearchParam searchParam) {
        return cacheStorage.search(searchParam, searchParam.getAppName(), searchParam.getPageIndex(), searchParam.getPageSize());
    }

    @RequestMapping("/com/netease/edu/kada/cache/core/manager/remove")
    @ResponseBody
    public void cacheRemove(String key) {
        projectCacheInvoke.afterCacheEvict(null, key, cacheWebProperties.getAppName());
    }

    @RequestMapping("/com/netease/edu/kada/cache/core/manager/remove/cacheName")
    @ResponseBody
    public void cacheRemoveConfig(SearchParam searchParam) {
        if (searchParam.getModel() == 1) {
            cacheStorage.removeClassName(searchParam.getClassName(), searchParam.getCacheKey(), searchParam.getAppName());
        } else {
            cacheStorage.removeCacheName(searchParam.getClassName(), searchParam.getCacheKey(), searchParam.getAppName());
        }
    }
}
