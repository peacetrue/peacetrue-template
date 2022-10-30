import {LocalModule, Module} from "./Module";
import installedModules from "./installedModules"
import localModules from "./localModules";

// 获取资源集合，包括安装的依赖资源和项目内的资源
export function getModules(): Module[] {
  let modules = [];
  // 本地模块，支持动态导出，因为本地代码都被编译了；安装模块，不支持动态导出，编译阶段未能识别，编译后的目标代码中缺失相关模块
  // let localModules = process.env.REACT_APP_LOCAL_MODULES;
  // localModules && modules.push(...localModules.split(",").map(module => (require(`./${module.trim()}`) as LocalModule).default));
  modules.push(...installedModules);
  modules.push(...localModules);
  if (modules.length === 0) modules.push((require(`./guesser`) as LocalModule).default);
  return modules;
}

export const resources = getModules().map(item => item.resource);

