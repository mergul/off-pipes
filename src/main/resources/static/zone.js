"use strict";
var __assign =
  (this && this.__assign) ||
  function () {
    return (
      (__assign =
        Object.assign ||
        function (e) {
          for (var t, n = 1, r = arguments.length; n < r; n++)
            for (var o in (t = arguments[n]))
              Object.prototype.hasOwnProperty.call(t, o) && (e[o] = t[o]);
          return e;
        }),
      __assign.apply(this, arguments)
    );
  };
/**
 * @license Angular v<unknown>
 * (c) 2010-2022 Google LLC. https://angular.io/
 * License: MIT
 */ !(function (e) {
  "function" == typeof define && define.amd ? define(e) : e();
})(function () {
  !(function (e) {
    var t = e.performance;
    function n(e) {
      t && t.mark && t.mark(e);
    }
    function r(e, n) {
      t && t.measure && t.measure(e, n);
    }
    n("Zone");
    var o = e.__Zone_symbol_prefix || "__zone_symbol__";
    function a(e) {
      return o + e;
    }
    var i = !0 === e[a("forceDuplicateZoneCheck")];
    if (e.Zone) {
      if (i || "function" != typeof e.Zone.__symbol__)
        throw new Error("Zone already loaded.");
      return e.Zone;
    }
    var s = (function () {
      function t(e, t) {
        (this._parent = e),
          (this._name = t ? t.name || "unnamed" : "<root>"),
          (this._properties = (t && t.properties) || {}),
          (this._zoneDelegate = new l(
            this,
            this._parent && this._parent._zoneDelegate,
            t
          ));
      }
      return (
        (t.assertZonePatched = function () {
          if (e.Promise !== C.ZoneAwarePromise)
            throw new Error(
              "Zone.js has detected that ZoneAwarePromise `(window|global).Promise` has been overwritten.\nMost likely cause is that a Promise polyfill has been loaded after Zone.js (Polyfilling Promise api is not necessary when zone.js is loaded. If you must load one, do so before loading zone.js.)"
            );
        }),
        Object.defineProperty(t, "root", {
          get: function () {
            for (var e = t.current; e.parent; ) e = e.parent;
            return e;
          },
          enumerable: !1,
          configurable: !0,
        }),
        Object.defineProperty(t, "current", {
          get: function () {
            return z.zone;
          },
          enumerable: !1,
          configurable: !0,
        }),
        Object.defineProperty(t, "currentTask", {
          get: function () {
            return I;
          },
          enumerable: !1,
          configurable: !0,
        }),
        (t.__load_patch = function (o, a, s) {
          if ((void 0 === s && (s = !1), C.hasOwnProperty(o))) {
            if (!s && i) throw Error("Already loaded patch: " + o);
          } else if (!e["__Zone_disable_" + o]) {
            var c = "Zone:" + o;
            n(c), (C[o] = a(e, t, j)), r(c, c);
          }
        }),
        Object.defineProperty(t.prototype, "parent", {
          get: function () {
            return this._parent;
          },
          enumerable: !1,
          configurable: !0,
        }),
        Object.defineProperty(t.prototype, "name", {
          get: function () {
            return this._name;
          },
          enumerable: !1,
          configurable: !0,
        }),
        (t.prototype.get = function (e) {
          var t = this.getZoneWith(e);
          if (t) return t._properties[e];
        }),
        (t.prototype.getZoneWith = function (e) {
          for (var t = this; t; ) {
            if (t._properties.hasOwnProperty(e)) return t;
            t = t._parent;
          }
          return null;
        }),
        (t.prototype.fork = function (e) {
          if (!e) throw new Error("ZoneSpec required!");
          return this._zoneDelegate.fork(this, e);
        }),
        (t.prototype.wrap = function (e, t) {
          if ("function" != typeof e)
            throw new Error("Expecting function got: " + e);
          var n = this._zoneDelegate.intercept(this, e, t),
            r = this;
          return function () {
            return r.runGuarded(n, this, arguments, t);
          };
        }),
        (t.prototype.run = function (e, t, n, r) {
          z = { parent: z, zone: this };
          try {
            return this._zoneDelegate.invoke(this, e, t, n, r);
          } finally {
            z = z.parent;
          }
        }),
        (t.prototype.runGuarded = function (e, t, n, r) {
          void 0 === t && (t = null), (z = { parent: z, zone: this });
          try {
            try {
              return this._zoneDelegate.invoke(this, e, t, n, r);
            } catch (e) {
              if (this._zoneDelegate.handleError(this, e)) throw e;
            }
          } finally {
            z = z.parent;
          }
        }),
        (t.prototype.runTask = function (e, t, n) {
          if (e.zone != this)
            throw new Error(
              "A task can only be run in the zone of creation! (Creation: " +
                (e.zone || m).name +
                "; Execution: " +
                this.name +
                ")"
            );
          if (e.state !== T || (e.type !== D && e.type !== O)) {
            var r = e.state != w;
            r && e._transitionTo(w, E), e.runCount++;
            var o = I;
            (I = e), (z = { parent: z, zone: this });
            try {
              e.type == O &&
                e.data &&
                !e.data.isPeriodic &&
                (e.cancelFn = void 0);
              try {
                return this._zoneDelegate.invokeTask(this, e, t, n);
              } catch (e) {
                if (this._zoneDelegate.handleError(this, e)) throw e;
              }
            } finally {
              e.state !== T &&
                e.state !== S &&
                (e.type == D || (e.data && e.data.isPeriodic)
                  ? r && e._transitionTo(E, w)
                  : ((e.runCount = 0),
                    this._updateTaskCount(e, -1),
                    r && e._transitionTo(T, w, T))),
                (z = z.parent),
                (I = o);
            }
          }
        }),
        (t.prototype.scheduleTask = function (e) {
          if (e.zone && e.zone !== this)
            for (var t = this; t; ) {
              if (t === e.zone)
                throw Error(
                  "can not reschedule task to "
                    .concat(
                      this.name,
                      " which is descendants of the original zone "
                    )
                    .concat(e.zone.name)
                );
              t = t.parent;
            }
          e._transitionTo(b, T);
          var n = [];
          (e._zoneDelegates = n), (e._zone = this);
          try {
            e = this._zoneDelegate.scheduleTask(this, e);
          } catch (t) {
            throw (
              (e._transitionTo(S, b, T),
              this._zoneDelegate.handleError(this, t),
              t)
            );
          }
          return (
            e._zoneDelegates === n && this._updateTaskCount(e, 1),
            e.state == b && e._transitionTo(E, b),
            e
          );
        }),
        (t.prototype.scheduleMicroTask = function (e, t, n, r) {
          return this.scheduleTask(new f(P, e, t, n, r, void 0));
        }),
        (t.prototype.scheduleMacroTask = function (e, t, n, r, o) {
          return this.scheduleTask(new f(O, e, t, n, r, o));
        }),
        (t.prototype.scheduleEventTask = function (e, t, n, r, o) {
          return this.scheduleTask(new f(D, e, t, n, r, o));
        }),
        (t.prototype.cancelTask = function (e) {
          if (e.zone != this)
            throw new Error(
              "A task can only be cancelled in the zone of creation! (Creation: " +
                (e.zone || m).name +
                "; Execution: " +
                this.name +
                ")"
            );
          if (e.state === E || e.state === w) {
            e._transitionTo(Z, E, w);
            try {
              this._zoneDelegate.cancelTask(this, e);
            } catch (t) {
              throw (
                (e._transitionTo(S, Z),
                this._zoneDelegate.handleError(this, t),
                t)
              );
            }
            return (
              this._updateTaskCount(e, -1),
              e._transitionTo(T, Z),
              (e.runCount = 0),
              e
            );
          }
        }),
        (t.prototype._updateTaskCount = function (e, t) {
          var n = e._zoneDelegates;
          -1 == t && (e._zoneDelegates = null);
          for (var r = 0; r < n.length; r++) n[r]._updateTaskCount(e.type, t);
        }),
        t
      );
    })();
    s.__symbol__ = a;
    var c,
      u = {
        name: "",
        onHasTask: function (e, t, n, r) {
          return e.hasTask(n, r);
        },
        onScheduleTask: function (e, t, n, r) {
          return e.scheduleTask(n, r);
        },
        onInvokeTask: function (e, t, n, r, o, a) {
          return e.invokeTask(n, r, o, a);
        },
        onCancelTask: function (e, t, n, r) {
          return e.cancelTask(n, r);
        },
      },
      l = (function () {
        function e(e, t, n) {
          (this._taskCounts = { microTask: 0, macroTask: 0, eventTask: 0 }),
            (this.zone = e),
            (this._parentDelegate = t),
            (this._forkZS = n && (n && n.onFork ? n : t._forkZS)),
            (this._forkDlgt = n && (n.onFork ? t : t._forkDlgt)),
            (this._forkCurrZone =
              n && (n.onFork ? this.zone : t._forkCurrZone)),
            (this._interceptZS = n && (n.onIntercept ? n : t._interceptZS)),
            (this._interceptDlgt = n && (n.onIntercept ? t : t._interceptDlgt)),
            (this._interceptCurrZone =
              n && (n.onIntercept ? this.zone : t._interceptCurrZone)),
            (this._invokeZS = n && (n.onInvoke ? n : t._invokeZS)),
            (this._invokeDlgt = n && (n.onInvoke ? t : t._invokeDlgt)),
            (this._invokeCurrZone =
              n && (n.onInvoke ? this.zone : t._invokeCurrZone)),
            (this._handleErrorZS =
              n && (n.onHandleError ? n : t._handleErrorZS)),
            (this._handleErrorDlgt =
              n && (n.onHandleError ? t : t._handleErrorDlgt)),
            (this._handleErrorCurrZone =
              n && (n.onHandleError ? this.zone : t._handleErrorCurrZone)),
            (this._scheduleTaskZS =
              n && (n.onScheduleTask ? n : t._scheduleTaskZS)),
            (this._scheduleTaskDlgt =
              n && (n.onScheduleTask ? t : t._scheduleTaskDlgt)),
            (this._scheduleTaskCurrZone =
              n && (n.onScheduleTask ? this.zone : t._scheduleTaskCurrZone)),
            (this._invokeTaskZS = n && (n.onInvokeTask ? n : t._invokeTaskZS)),
            (this._invokeTaskDlgt =
              n && (n.onInvokeTask ? t : t._invokeTaskDlgt)),
            (this._invokeTaskCurrZone =
              n && (n.onInvokeTask ? this.zone : t._invokeTaskCurrZone)),
            (this._cancelTaskZS = n && (n.onCancelTask ? n : t._cancelTaskZS)),
            (this._cancelTaskDlgt =
              n && (n.onCancelTask ? t : t._cancelTaskDlgt)),
            (this._cancelTaskCurrZone =
              n && (n.onCancelTask ? this.zone : t._cancelTaskCurrZone)),
            (this._hasTaskZS = null),
            (this._hasTaskDlgt = null),
            (this._hasTaskDlgtOwner = null),
            (this._hasTaskCurrZone = null);
          var r = n && n.onHasTask;
          (r || (t && t._hasTaskZS)) &&
            ((this._hasTaskZS = r ? n : u),
            (this._hasTaskDlgt = t),
            (this._hasTaskDlgtOwner = this),
            (this._hasTaskCurrZone = e),
            n.onScheduleTask ||
              ((this._scheduleTaskZS = u),
              (this._scheduleTaskDlgt = t),
              (this._scheduleTaskCurrZone = this.zone)),
            n.onInvokeTask ||
              ((this._invokeTaskZS = u),
              (this._invokeTaskDlgt = t),
              (this._invokeTaskCurrZone = this.zone)),
            n.onCancelTask ||
              ((this._cancelTaskZS = u),
              (this._cancelTaskDlgt = t),
              (this._cancelTaskCurrZone = this.zone)));
        }
        return (
          (e.prototype.fork = function (e, t) {
            return this._forkZS
              ? this._forkZS.onFork(this._forkDlgt, this.zone, e, t)
              : new s(e, t);
          }),
          (e.prototype.intercept = function (e, t, n) {
            return this._interceptZS
              ? this._interceptZS.onIntercept(
                  this._interceptDlgt,
                  this._interceptCurrZone,
                  e,
                  t,
                  n
                )
              : t;
          }),
          (e.prototype.invoke = function (e, t, n, r, o) {
            return this._invokeZS
              ? this._invokeZS.onInvoke(
                  this._invokeDlgt,
                  this._invokeCurrZone,
                  e,
                  t,
                  n,
                  r,
                  o
                )
              : t.apply(n, r);
          }),
          (e.prototype.handleError = function (e, t) {
            return (
              !this._handleErrorZS ||
              this._handleErrorZS.onHandleError(
                this._handleErrorDlgt,
                this._handleErrorCurrZone,
                e,
                t
              )
            );
          }),
          (e.prototype.scheduleTask = function (e, t) {
            var n = t;
            if (this._scheduleTaskZS)
              this._hasTaskZS && n._zoneDelegates.push(this._hasTaskDlgtOwner),
                (n = this._scheduleTaskZS.onScheduleTask(
                  this._scheduleTaskDlgt,
                  this._scheduleTaskCurrZone,
                  e,
                  t
                )) || (n = t);
            else if (t.scheduleFn) t.scheduleFn(t);
            else {
              if (t.type != P) throw new Error("Task is missing scheduleFn.");
              y(t);
            }
            return n;
          }),
          (e.prototype.invokeTask = function (e, t, n, r) {
            return this._invokeTaskZS
              ? this._invokeTaskZS.onInvokeTask(
                  this._invokeTaskDlgt,
                  this._invokeTaskCurrZone,
                  e,
                  t,
                  n,
                  r
                )
              : t.callback.apply(n, r);
          }),
          (e.prototype.cancelTask = function (e, t) {
            var n;
            if (this._cancelTaskZS)
              n = this._cancelTaskZS.onCancelTask(
                this._cancelTaskDlgt,
                this._cancelTaskCurrZone,
                e,
                t
              );
            else {
              if (!t.cancelFn) throw Error("Task is not cancelable");
              n = t.cancelFn(t);
            }
            return n;
          }),
          (e.prototype.hasTask = function (e, t) {
            try {
              this._hasTaskZS &&
                this._hasTaskZS.onHasTask(
                  this._hasTaskDlgt,
                  this._hasTaskCurrZone,
                  e,
                  t
                );
            } catch (t) {
              this.handleError(e, t);
            }
          }),
          (e.prototype._updateTaskCount = function (e, t) {
            var n = this._taskCounts,
              r = n[e],
              o = (n[e] = r + t);
            if (o < 0)
              throw new Error("More tasks executed then were scheduled.");
            (0 != r && 0 != o) ||
              this.hasTask(this.zone, {
                microTask: n.microTask > 0,
                macroTask: n.macroTask > 0,
                eventTask: n.eventTask > 0,
                change: e,
              });
          }),
          e
        );
      })(),
      f = (function () {
        function t(n, r, o, a, i, s) {
          if (
            ((this._zone = null),
            (this.runCount = 0),
            (this._zoneDelegates = null),
            (this._state = "notScheduled"),
            (this.type = n),
            (this.source = r),
            (this.data = a),
            (this.scheduleFn = i),
            (this.cancelFn = s),
            !o)
          )
            throw new Error("callback is not defined");
          this.callback = o;
          var c = this;
          this.invoke =
            n === D && a && a.useG
              ? t.invokeTask
              : function () {
                  return t.invokeTask.call(e, c, this, arguments);
                };
        }
        return (
          (t.invokeTask = function (e, t, n) {
            e || (e = this), R++;
            try {
              return e.runCount++, e.zone.runTask(e, t, n);
            } finally {
              1 == R && k(), R--;
            }
          }),
          Object.defineProperty(t.prototype, "zone", {
            get: function () {
              return this._zone;
            },
            enumerable: !1,
            configurable: !0,
          }),
          Object.defineProperty(t.prototype, "state", {
            get: function () {
              return this._state;
            },
            enumerable: !1,
            configurable: !0,
          }),
          (t.prototype.cancelScheduleRequest = function () {
            this._transitionTo(T, b);
          }),
          (t.prototype._transitionTo = function (e, t, n) {
            if (this._state !== t && this._state !== n)
              throw new Error(
                ""
                  .concat(this.type, " '")
                  .concat(this.source, "': can not transition to '")
                  .concat(e, "', expecting state '")
                  .concat(t, "'")
                  .concat(n ? " or '" + n + "'" : "", ", was '")
                  .concat(this._state, "'.")
              );
            (this._state = e), e == T && (this._zoneDelegates = null);
          }),
          (t.prototype.toString = function () {
            return this.data && void 0 !== this.data.handleId
              ? this.data.handleId.toString()
              : Object.prototype.toString.call(this);
          }),
          (t.prototype.toJSON = function () {
            return {
              type: this.type,
              state: this.state,
              source: this.source,
              zone: this.zone.name,
              runCount: this.runCount,
            };
          }),
          t
        );
      })(),
      h = a("setTimeout"),
      p = a("Promise"),
      v = a("then"),
      d = [],
      _ = !1;
    function g(t) {
      if ((c || (e[p] && (c = e[p].resolve(0))), c)) {
        var n = c[v];
        n || (n = c.then), n.call(c, t);
      } else e[h](t, 0);
    }
    function y(e) {
      0 === R && 0 === d.length && g(k), e && d.push(e);
    }
    function k() {
      if (!_) {
        for (_ = !0; d.length; ) {
          var e = d;
          d = [];
          for (var t = 0; t < e.length; t++) {
            var n = e[t];
            try {
              n.zone.runTask(n, null, null);
            } catch (e) {
              j.onUnhandledError(e);
            }
          }
        }
        j.microtaskDrainDone(), (_ = !1);
      }
    }
    var m = { name: "NO ZONE" },
      T = "notScheduled",
      b = "scheduling",
      E = "scheduled",
      w = "running",
      Z = "canceling",
      S = "unknown",
      P = "microTask",
      O = "macroTask",
      D = "eventTask",
      C = {},
      j = {
        symbol: a,
        currentZoneFrame: function () {
          return z;
        },
        onUnhandledError: M,
        microtaskDrainDone: M,
        scheduleMicroTask: y,
        showUncaughtError: function () {
          return !s[a("ignoreConsoleErrorUncaughtError")];
        },
        patchEventTarget: function () {
          return [];
        },
        patchOnProperties: M,
        patchMethod: function () {
          return M;
        },
        bindArguments: function () {
          return [];
        },
        patchThen: function () {
          return M;
        },
        patchMacroTask: function () {
          return M;
        },
        patchEventPrototype: function () {
          return M;
        },
        isIEOrEdge: function () {
          return !1;
        },
        getGlobalObjects: function () {},
        ObjectDefineProperty: function () {
          return M;
        },
        ObjectGetOwnPropertyDescriptor: function () {},
        ObjectCreate: function () {},
        ArraySlice: function () {
          return [];
        },
        patchClass: function () {
          return M;
        },
        wrapWithCurrentZone: function () {
          return M;
        },
        filterProperties: function () {
          return [];
        },
        attachOriginToPatched: function () {
          return M;
        },
        _redefineProperty: function () {
          return M;
        },
        patchCallbacks: function () {
          return M;
        },
        nativeScheduleMicroTask: g,
      },
      z = { parent: null, zone: new s(null, null) },
      I = null,
      R = 0;
    function M() {}
    r("Zone", "Zone"), (e.Zone = s);
  })(
    ("undefined" != typeof window && window) ||
      ("undefined" != typeof self && self) ||
      global
  );
  var e = Object.getOwnPropertyDescriptor,
    t = Object.defineProperty,
    n = Object.getPrototypeOf,
    r = Object.create,
    o = Array.prototype.slice,
    a = "addEventListener",
    i = "removeEventListener",
    s = Zone.__symbol__(a),
    c = Zone.__symbol__(i),
    u = "true",
    l = "false",
    f = Zone.__symbol__("");
  function h(e, t) {
    return Zone.current.wrap(e, t);
  }
  function p(e, t, n, r, o) {
    return Zone.current.scheduleMacroTask(e, t, n, r, o);
  }
  var v = Zone.__symbol__,
    d = "undefined" != typeof window,
    _ = d ? window : void 0,
    g = (d && _) || ("object" == typeof self && self) || global,
    y = "removeAttribute";
  function k(e, t) {
    for (var n = e.length - 1; n >= 0; n--)
      "function" == typeof e[n] && (e[n] = h(e[n], t + "_" + n));
    return e;
  }
  function m(e) {
    return (
      !e ||
      (!1 !== e.writable && !("function" == typeof e.get && void 0 === e.set))
    );
  }
  var T =
      "undefined" != typeof WorkerGlobalScope &&
      self instanceof WorkerGlobalScope,
    b =
      !("nw" in g) &&
      void 0 !== g.process &&
      "[object process]" === {}.toString.call(g.process),
    E = !b && !T && !(!d || !_.HTMLElement),
    w =
      void 0 !== g.process &&
      "[object process]" === {}.toString.call(g.process) &&
      !T &&
      !(!d || !_.HTMLElement),
    Z = {},
    S = function (e) {
      if ((e = e || g.event)) {
        var t = Z[e.type];
        t || (t = Z[e.type] = v("ON_PROPERTY" + e.type));
        var n,
          r = this || e.target || g,
          o = r[t];
        return (
          E && r === _ && "error" === e.type
            ? !0 ===
                (n =
                  o &&
                  o.call(
                    this,
                    e.message,
                    e.filename,
                    e.lineno,
                    e.colno,
                    e.error
                  )) && e.preventDefault()
            : null == (n = o && o.apply(this, arguments)) ||
              n ||
              e.preventDefault(),
          n
        );
      }
    };
  function P(n, r, o) {
    var a = e(n, r);
    if (
      (!a && o && e(o, r) && (a = { enumerable: !0, configurable: !0 }),
      a && a.configurable)
    ) {
      var i = v("on" + r + "patched");
      if (!n.hasOwnProperty(i) || !n[i]) {
        delete a.writable, delete a.value;
        var s = a.get,
          c = a.set,
          u = r.slice(2),
          l = Z[u];
        l || (l = Z[u] = v("ON_PROPERTY" + u)),
          (a.set = function (e) {
            var t = this;
            t || n !== g || (t = g),
              t &&
                ("function" == typeof t[l] && t.removeEventListener(u, S),
                c && c.call(t, null),
                (t[l] = e),
                "function" == typeof e && t.addEventListener(u, S, !1));
          }),
          (a.get = function () {
            var e = this;
            if ((e || n !== g || (e = g), !e)) return null;
            var t = e[l];
            if (t) return t;
            if (s) {
              var o = s.call(this);
              if (o)
                return (
                  a.set.call(this, o),
                  "function" == typeof e[y] && e.removeAttribute(r),
                  o
                );
            }
            return null;
          }),
          t(n, r, a),
          (n[i] = !0);
      }
    }
  }
  function O(e, t, n) {
    if (t) for (var r = 0; r < t.length; r++) P(e, "on" + t[r], n);
    else {
      var o = [];
      for (var a in e) "on" == a.slice(0, 2) && o.push(a);
      for (var i = 0; i < o.length; i++) P(e, o[i], n);
    }
  }
  var D = v("originalInstance");
  function C(e) {
    var n = g[e];
    if (n) {
      (g[v(e)] = n),
        (g[e] = function () {
          var t = k(arguments, e);
          switch (t.length) {
            case 0:
              this[D] = new n();
              break;
            case 1:
              this[D] = new n(t[0]);
              break;
            case 2:
              this[D] = new n(t[0], t[1]);
              break;
            case 3:
              this[D] = new n(t[0], t[1], t[2]);
              break;
            case 4:
              this[D] = new n(t[0], t[1], t[2], t[3]);
              break;
            default:
              throw new Error("Arg list too long.");
          }
        }),
        I(g[e], n);
      var r,
        o = new n(function () {});
      for (r in o)
        ("XMLHttpRequest" === e && "responseBlob" === r) ||
          (function (n) {
            "function" == typeof o[n]
              ? (g[e].prototype[n] = function () {
                  return this[D][n].apply(this[D], arguments);
                })
              : t(g[e].prototype, n, {
                  set: function (t) {
                    "function" == typeof t
                      ? ((this[D][n] = h(t, e + "." + n)), I(this[D][n], t))
                      : (this[D][n] = t);
                  },
                  get: function () {
                    return this[D][n];
                  },
                });
          })(r);
      for (r in n) "prototype" !== r && n.hasOwnProperty(r) && (g[e][r] = n[r]);
    }
  }
  function j(t, r, o) {
    for (var a = t; a && !a.hasOwnProperty(r); ) a = n(a);
    !a && t[r] && (a = t);
    var i = v(r),
      s = null;
    if (
      a &&
      (!(s = a[i]) || !a.hasOwnProperty(i)) &&
      ((s = a[i] = a[r]), m(a && e(a, r)))
    ) {
      var c = o(s, i, r);
      (a[r] = function () {
        return c(this, arguments);
      }),
        I(a[r], s);
    }
    return s;
  }
  function z(e, t, n) {
    var r = null;
    function o(e) {
      var t = e.data;
      return (
        (t.args[t.cbIdx] = function () {
          e.invoke.apply(this, arguments);
        }),
        r.apply(t.target, t.args),
        e
      );
    }
    r = j(e, t, function (e) {
      return function (t, r) {
        var a = n(t, r);
        return a.cbIdx >= 0 && "function" == typeof r[a.cbIdx]
          ? p(a.name, r[a.cbIdx], a, o)
          : e.apply(t, r);
      };
    });
  }
  function I(e, t) {
    e[v("OriginalDelegate")] = t;
  }
  var R = !1,
    M = !1;
  function N() {
    if (R) return M;
    R = !0;
    try {
      var e = _.navigator.userAgent;
      (-1 === e.indexOf("MSIE ") &&
        -1 === e.indexOf("Trident/") &&
        -1 === e.indexOf("Edge/")) ||
        (M = !0);
    } catch (e) {}
    return M;
  }
  Zone.__load_patch("ZoneAwarePromise", function (e, t, n) {
    var r = Object.getOwnPropertyDescriptor,
      o = Object.defineProperty,
      a = n.symbol,
      i = [],
      s = !0 === e[a("DISABLE_WRAPPING_UNCAUGHT_PROMISE_REJECTION")],
      c = a("Promise"),
      u = a("then"),
      l = "__creationTrace__";
    (n.onUnhandledError = function (e) {
      if (n.showUncaughtError()) {
        var t = e && e.rejection;
        t
          ? console.error(
              "Unhandled Promise rejection:",
              t instanceof Error ? t.message : t,
              "; Zone:",
              e.zone.name,
              "; Task:",
              e.task && e.task.source,
              "; Value:",
              t,
              t instanceof Error ? t.stack : void 0
            )
          : console.error(e);
      }
    }),
      (n.microtaskDrainDone = function () {
        for (
          var e = function () {
            var e = i.shift();
            try {
              e.zone.runGuarded(function () {
                if (e.throwOriginal) throw e.rejection;
                throw e;
              });
            } catch (e) {
              !(function r(e) {
                n.onUnhandledError(e);
                try {
                  var r = t[f];
                  "function" == typeof r && r.call(this, e);
                } catch (e) {}
              })(e);
            }
          };
          i.length;

        )
          e();
      });
    var f = a("unhandledPromiseRejectionHandler");
    function h(e) {
      return e && e.then;
    }
    function p(e) {
      return e;
    }
    function v(e) {
      return N.reject(e);
    }
    var d = a("state"),
      _ = a("value"),
      g = a("finally"),
      y = a("parentPromiseValue"),
      k = a("parentPromiseState"),
      m = "Promise.then",
      T = null,
      b = !0,
      E = !1,
      w = 0;
    function Z(e, t) {
      return function (n) {
        try {
          D(e, t, n);
        } catch (t) {
          D(e, !1, t);
        }
      };
    }
    var S = function () {
        var e = !1;
        return function t(n) {
          return function () {
            e || ((e = !0), n.apply(null, arguments));
          };
        };
      },
      P = "Promise resolved with itself",
      O = a("currentTaskTrace");
    function D(e, r, a) {
      var c = S();
      if (e === a) throw new TypeError(P);
      if (e[d] === T) {
        var u = null;
        try {
          ("object" != typeof a && "function" != typeof a) || (u = a && a.then);
        } catch (t) {
          return (
            c(function () {
              D(e, !1, t);
            })(),
            e
          );
        }
        if (
          r !== E &&
          a instanceof N &&
          a.hasOwnProperty(d) &&
          a.hasOwnProperty(_) &&
          a[d] !== T
        )
          z(a), D(e, a[d], a[_]);
        else if (r !== E && "function" == typeof u)
          try {
            u.call(a, c(Z(e, r)), c(Z(e, !1)));
          } catch (t) {
            c(function () {
              D(e, !1, t);
            })();
          }
        else {
          e[d] = r;
          var f = e[_];
          if (
            ((e[_] = a),
            e[g] === g && r === b && ((e[d] = e[k]), (e[_] = e[y])),
            r === E && a instanceof Error)
          ) {
            var h =
              t.currentTask && t.currentTask.data && t.currentTask.data[l];
            h &&
              o(a, O, {
                configurable: !0,
                enumerable: !1,
                writable: !0,
                value: h,
              });
          }
          for (var p = 0; p < f.length; ) I(e, f[p++], f[p++], f[p++], f[p++]);
          if (0 == f.length && r == E) {
            e[d] = w;
            var v = a;
            try {
              throw new Error(
                "Uncaught (in promise): " +
                  (function e(t) {
                    return t && t.toString === Object.prototype.toString
                      ? ((t.constructor && t.constructor.name) || "") +
                          ": " +
                          JSON.stringify(t)
                      : t
                      ? t.toString()
                      : Object.prototype.toString.call(t);
                  })(a) +
                  (a && a.stack ? "\n" + a.stack : "")
              );
            } catch (e) {
              v = e;
            }
            s && (v.throwOriginal = !0),
              (v.rejection = a),
              (v.promise = e),
              (v.zone = t.current),
              (v.task = t.currentTask),
              i.push(v),
              n.scheduleMicroTask();
          }
        }
      }
      return e;
    }
    var C = a("rejectionHandledHandler");
    function z(e) {
      if (e[d] === w) {
        try {
          var n = t[C];
          n &&
            "function" == typeof n &&
            n.call(this, { rejection: e[_], promise: e });
        } catch (e) {}
        e[d] = E;
        for (var r = 0; r < i.length; r++) e === i[r].promise && i.splice(r, 1);
      }
    }
    function I(e, t, n, r, o) {
      z(e);
      var a = e[d],
        i = a
          ? "function" == typeof r
            ? r
            : p
          : "function" == typeof o
          ? o
          : v;
      t.scheduleMicroTask(
        m,
        function () {
          try {
            var r = e[_],
              o = !!n && g === n[g];
            o && ((n[y] = r), (n[k] = a));
            var s = t.run(i, void 0, o && i !== v && i !== p ? [] : [r]);
            D(n, !0, s);
          } catch (e) {
            D(n, !1, e);
          }
        },
        n
      );
    }
    var R = function () {},
      M = e.AggregateError,
      N = (function () {
        function e(t) {
          var n = this;
          if (!(n instanceof e))
            throw new Error("Must be an instanceof Promise.");
          (n[d] = T), (n[_] = []);
          try {
            var r = S();
            t && t(r(Z(n, b)), r(Z(n, E)));
          } catch (e) {
            D(n, !1, e);
          }
        }
        return (
          (e.toString = function () {
            return "function ZoneAwarePromise() { [native code] }";
          }),
          (e.resolve = function (e) {
            return D(new this(null), b, e);
          }),
          (e.reject = function (e) {
            return D(new this(null), E, e);
          }),
          (e.any = function (t) {
            if (!t || "function" != typeof t[Symbol.iterator])
              return Promise.reject(new M([], "All promises were rejected"));
            var n = [],
              r = 0;
            try {
              for (var o = 0, a = t; o < a.length; o++)
                r++, n.push(e.resolve(a[o]));
            } catch (e) {
              return Promise.reject(new M([], "All promises were rejected"));
            }
            if (0 === r)
              return Promise.reject(new M([], "All promises were rejected"));
            var i = !1,
              s = [];
            return new e(function (e, t) {
              for (var o = 0; o < n.length; o++)
                n[o].then(
                  function (t) {
                    i || ((i = !0), e(t));
                  },
                  function (e) {
                    s.push(e),
                      0 == --r &&
                        ((i = !0), t(new M(s, "All promises were rejected")));
                  }
                );
            });
          }),
          (e.race = function (e) {
            var t,
              n,
              r = new this(function (e, r) {
                (t = e), (n = r);
              });
            function o(e) {
              t(e);
            }
            function a(e) {
              n(e);
            }
            for (var i = 0, s = e; i < s.length; i++) {
              var c = s[i];
              h(c) || (c = this.resolve(c)), c.then(o, a);
            }
            return r;
          }),
          (e.all = function (t) {
            return e.allWithCallback(t);
          }),
          (e.allSettled = function (t) {
            return (
              this && this.prototype instanceof e ? this : e
            ).allWithCallback(t, {
              thenCallback: function (e) {
                return { status: "fulfilled", value: e };
              },
              errorCallback: function (e) {
                return { status: "rejected", reason: e };
              },
            });
          }),
          (e.allWithCallback = function (e, t) {
            for (
              var n,
                r,
                o = new this(function (e, t) {
                  (n = e), (r = t);
                }),
                a = 2,
                i = 0,
                s = [],
                c = function (e) {
                  h(e) || (e = u.resolve(e));
                  var o = i;
                  try {
                    e.then(
                      function (e) {
                        (s[o] = t ? t.thenCallback(e) : e), 0 == --a && n(s);
                      },
                      function (e) {
                        t
                          ? ((s[o] = t.errorCallback(e)), 0 == --a && n(s))
                          : r(e);
                      }
                    );
                  } catch (e) {
                    r(e);
                  }
                  a++, i++;
                },
                u = this,
                l = 0,
                f = e;
              l < f.length;
              l++
            )
              c(f[l]);
            return 0 == (a -= 2) && n(s), o;
          }),
          Object.defineProperty(e.prototype, Symbol.toStringTag, {
            get: function () {
              return "Promise";
            },
            enumerable: !1,
            configurable: !0,
          }),
          Object.defineProperty(e.prototype, Symbol.species, {
            get: function () {
              return e;
            },
            enumerable: !1,
            configurable: !0,
          }),
          (e.prototype.then = function (n, r) {
            var o,
              a =
                null === (o = this.constructor) || void 0 === o
                  ? void 0
                  : o[Symbol.species];
            (a && "function" == typeof a) || (a = this.constructor || e);
            var i = new a(R),
              s = t.current;
            return (
              this[d] == T ? this[_].push(s, i, n, r) : I(this, s, i, n, r), i
            );
          }),
          (e.prototype.catch = function (e) {
            return this.then(null, e);
          }),
          (e.prototype.finally = function (n) {
            var r,
              o =
                null === (r = this.constructor) || void 0 === r
                  ? void 0
                  : r[Symbol.species];
            (o && "function" == typeof o) || (o = e);
            var a = new o(R);
            a[g] = g;
            var i = t.current;
            return (
              this[d] == T ? this[_].push(i, a, n, n) : I(this, i, a, n, n), a
            );
          }),
          e
        );
      })();
    (N.resolve = N.resolve),
      (N.reject = N.reject),
      (N.race = N.race),
      (N.all = N.all);
    var A = (e[c] = e.Promise);
    e.Promise = N;
    var L = a("thenPatched");
    function H(e) {
      var t = e.prototype,
        n = r(t, "then");
      if (!n || (!1 !== n.writable && n.configurable)) {
        var o = t.then;
        (t[u] = o),
          (e.prototype.then = function (e, t) {
            var n = this;
            return new N(function (e, t) {
              o.call(n, e, t);
            }).then(e, t);
          }),
          (e[L] = !0);
      }
    }
    return (
      (n.patchThen = H),
      A &&
        (H(A),
        j(e, "fetch", function (e) {
          return (function t(e) {
            return function (t, n) {
              var r = e.apply(t, n);
              if (r instanceof N) return r;
              var o = r.constructor;
              return o[L] || H(o), r;
            };
          })(e);
        })),
      (Promise[t.__symbol__("uncaughtPromiseErrors")] = i),
      N
    );
  }),
    Zone.__load_patch("toString", function (e) {
      var t = Function.prototype.toString,
        n = v("OriginalDelegate"),
        r = v("Promise"),
        o = v("Error"),
        a = function a() {
          if ("function" == typeof this) {
            var i = this[n];
            if (i)
              return "function" == typeof i
                ? t.call(i)
                : Object.prototype.toString.call(i);
            if (this === Promise) {
              var s = e[r];
              if (s) return t.call(s);
            }
            if (this === Error) {
              var c = e[o];
              if (c) return t.call(c);
            }
          }
          return t.call(this);
        };
      (a[n] = t), (Function.prototype.toString = a);
      var i = Object.prototype.toString;
      Object.prototype.toString = function () {
        return "function" == typeof Promise && this instanceof Promise
          ? "[object Promise]"
          : i.call(this);
      };
    });
  var A = !1;
  if ("undefined" != typeof window)
    try {
      var L = Object.defineProperty({}, "passive", {
        get: function () {
          A = !0;
        },
      });
      window.addEventListener("test", L, L),
        window.removeEventListener("test", L, L);
    } catch (e) {
      A = !1;
    }
  var H = { useG: !0 },
    x = {},
    F = {},
    q = new RegExp("^" + f + "(\\w+)(true|false)$"),
    G = v("propagationStopped");
  function W(e, t) {
    var n = (t ? t(e) : e) + l,
      r = (t ? t(e) : e) + u,
      o = f + n,
      a = f + r;
    (x[e] = {}), (x[e][l] = o), (x[e][u] = a);
  }
  function B(e, t, r, o) {
    var s = (o && o.add) || a,
      c = (o && o.rm) || i,
      h = (o && o.listeners) || "eventListeners",
      p = (o && o.rmAll) || "removeAllListeners",
      d = v(s),
      _ = "." + s + ":",
      g = "prependListener",
      y = "." + g + ":",
      k = function (e, t, n) {
        if (!e.isRemoved) {
          var r,
            o = e.callback;
          "object" == typeof o &&
            o.handleEvent &&
            ((e.callback = function (e) {
              return o.handleEvent(e);
            }),
            (e.originalDelegate = o));
          try {
            e.invoke(e, t, [n]);
          } catch (e) {
            r = e;
          }
          var a = e.options;
          return (
            a &&
              "object" == typeof a &&
              a.once &&
              t[c].call(
                t,
                n.type,
                e.originalDelegate ? e.originalDelegate : e.callback,
                a
              ),
            r
          );
        }
      };
    function m(n, r, o) {
      if ((r = r || e.event)) {
        var a = n || r.target || e,
          i = a[x[r.type][o ? u : l]];
        if (i) {
          var s = [];
          if (1 === i.length) (h = k(i[0], a, r)) && s.push(h);
          else
            for (
              var c = i.slice(), f = 0;
              f < c.length && (!r || !0 !== r[G]);
              f++
            ) {
              var h;
              (h = k(c[f], a, r)) && s.push(h);
            }
          if (1 === s.length) throw s[0];
          var p = function (e) {
            var n = s[e];
            t.nativeScheduleMicroTask(function () {
              throw n;
            });
          };
          for (f = 0; f < s.length; f++) p(f);
        }
      }
    }
    var T = function (e) {
        return m(this, e, !1);
      },
      E = function (e) {
        return m(this, e, !0);
      };
    function w(t, r) {
      if (!t) return !1;
      var o = !0;
      r && void 0 !== r.useG && (o = r.useG);
      var a = r && r.vh,
        i = !0;
      r && void 0 !== r.chkDup && (i = r.chkDup);
      var k = !1;
      r && void 0 !== r.rt && (k = r.rt);
      for (var m = t; m && !m.hasOwnProperty(s); ) m = n(m);
      if ((!m && t[s] && (m = t), !m)) return !1;
      if (m[d]) return !1;
      var w,
        Z = r && r.eventNameToString,
        S = {},
        P = (m[d] = m[s]),
        O = (m[v(c)] = m[c]),
        D = (m[v(h)] = m[h]),
        C = (m[v(p)] = m[p]);
      r && r.prepend && (w = m[v(r.prepend)] = m[r.prepend]);
      var j = o
          ? function (e) {
              if (!S.isExisting)
                return P.call(
                  S.target,
                  S.eventName,
                  S.capture ? E : T,
                  S.options
                );
            }
          : function (e) {
              return P.call(S.target, S.eventName, e.invoke, S.options);
            },
        z = o
          ? function (e) {
              if (!e.isRemoved) {
                var t = x[e.eventName],
                  n = void 0;
                t && (n = t[e.capture ? u : l]);
                var r = n && e.target[n];
                if (r)
                  for (var o = 0; o < r.length; o++)
                    if (r[o] === e) {
                      r.splice(o, 1),
                        (e.isRemoved = !0),
                        0 === r.length &&
                          ((e.allRemoved = !0), (e.target[n] = null));
                      break;
                    }
              }
              if (e.allRemoved)
                return O.call(
                  e.target,
                  e.eventName,
                  e.capture ? E : T,
                  e.options
                );
            }
          : function (e) {
              return O.call(e.target, e.eventName, e.invoke, e.options);
            },
        R =
          r && r.diff
            ? r.diff
            : function (e, t) {
                var n = typeof t;
                return (
                  ("function" === n && e.callback === t) ||
                  ("object" === n && e.originalDelegate === t)
                );
              },
        M = Zone[v("UNPATCHED_EVENTS")],
        N = e[v("PASSIVE_EVENTS")],
        L = function (t, n, s, c, f, h) {
          return (
            void 0 === f && (f = !1),
            void 0 === h && (h = !1),
            function () {
              var p = this || e,
                v = arguments[0];
              r && r.transferEventName && (v = r.transferEventName(v));
              var d = arguments[1];
              if (!d) return t.apply(this, arguments);
              if (b && "uncaughtException" === v)
                return t.apply(this, arguments);
              var _ = !1;
              if ("function" != typeof d) {
                if (!d.handleEvent) return t.apply(this, arguments);
                _ = !0;
              }
              if (!a || a(t, d, p, arguments)) {
                var g = A && !!N && -1 !== N.indexOf(v),
                  y = (function e(t, n) {
                    return !A && "object" == typeof t && t
                      ? !!t.capture
                      : A && n
                      ? "boolean" == typeof t
                        ? { capture: t, passive: !0 }
                        : t
                        ? "object" == typeof t && !1 !== t.passive
                          ? __assign(__assign({}, t), { passive: !0 })
                          : t
                        : { passive: !0 }
                      : t;
                  })(arguments[2], g);
                if (M)
                  for (var k = 0; k < M.length; k++)
                    if (v === M[k])
                      return g ? t.call(p, v, d, y) : t.apply(this, arguments);
                var m = !!y && ("boolean" == typeof y || y.capture),
                  T = !(!y || "object" != typeof y) && y.once,
                  E = Zone.current,
                  w = x[v];
                w || (W(v, Z), (w = x[v]));
                var P,
                  O = w[m ? u : l],
                  D = p[O],
                  C = !1;
                if (D) {
                  if (((C = !0), i))
                    for (k = 0; k < D.length; k++) if (R(D[k], d)) return;
                } else D = p[O] = [];
                var j = p.constructor.name,
                  z = F[j];
                z && (P = z[v]),
                  P || (P = j + n + (Z ? Z(v) : v)),
                  (S.options = y),
                  T && (S.options.once = !1),
                  (S.target = p),
                  (S.capture = m),
                  (S.eventName = v),
                  (S.isExisting = C);
                var I = o ? H : void 0;
                I && (I.taskData = S);
                var L = E.scheduleEventTask(P, d, I, s, c);
                return (
                  (S.target = null),
                  I && (I.taskData = null),
                  T && (y.once = !0),
                  (A || "boolean" != typeof L.options) && (L.options = y),
                  (L.target = p),
                  (L.capture = m),
                  (L.eventName = v),
                  _ && (L.originalDelegate = d),
                  h ? D.unshift(L) : D.push(L),
                  f ? p : void 0
                );
              }
            }
          );
        };
      return (
        (m[s] = L(P, _, j, z, k)),
        w &&
          (m[g] = L(
            w,
            y,
            function (e) {
              return w.call(S.target, S.eventName, e.invoke, S.options);
            },
            z,
            k,
            !0
          )),
        (m[c] = function () {
          var t = this || e,
            n = arguments[0];
          r && r.transferEventName && (n = r.transferEventName(n));
          var o = arguments[2],
            i = !!o && ("boolean" == typeof o || o.capture),
            s = arguments[1];
          if (!s) return O.apply(this, arguments);
          if (!a || a(O, s, t, arguments)) {
            var c,
              h = x[n];
            h && (c = h[i ? u : l]);
            var p = c && t[c];
            if (p)
              for (var v = 0; v < p.length; v++) {
                var d = p[v];
                if (R(d, s))
                  return (
                    p.splice(v, 1),
                    (d.isRemoved = !0),
                    0 === p.length &&
                      ((d.allRemoved = !0),
                      (t[c] = null),
                      "string" == typeof n &&
                        (t[f + "ON_PROPERTY" + n] = null)),
                    d.zone.cancelTask(d),
                    k ? t : void 0
                  );
              }
            return O.apply(this, arguments);
          }
        }),
        (m[h] = function () {
          var t = this || e,
            n = arguments[0];
          r && r.transferEventName && (n = r.transferEventName(n));
          for (var o = [], a = U(t, Z ? Z(n) : n), i = 0; i < a.length; i++) {
            var s = a[i];
            o.push(s.originalDelegate ? s.originalDelegate : s.callback);
          }
          return o;
        }),
        (m[p] = function () {
          var t = this || e,
            n = arguments[0];
          if (n) {
            r && r.transferEventName && (n = r.transferEventName(n));
            var o = x[n];
            if (o) {
              var a = t[o[l]],
                i = t[o[u]];
              if (a) {
                var s = a.slice();
                for (v = 0; v < s.length; v++)
                  this[c].call(
                    this,
                    n,
                    (f = s[v]).originalDelegate
                      ? f.originalDelegate
                      : f.callback,
                    f.options
                  );
              }
              if (i)
                for (s = i.slice(), v = 0; v < s.length; v++) {
                  var f;
                  this[c].call(
                    this,
                    n,
                    (f = s[v]).originalDelegate
                      ? f.originalDelegate
                      : f.callback,
                    f.options
                  );
                }
            }
          } else {
            for (var h = Object.keys(t), v = 0; v < h.length; v++) {
              var d = q.exec(h[v]),
                _ = d && d[1];
              _ && "removeListener" !== _ && this[p].call(this, _);
            }
            this[p].call(this, "removeListener");
          }
          if (k) return this;
        }),
        I(m[s], P),
        I(m[c], O),
        C && I(m[p], C),
        D && I(m[h], D),
        !0
      );
    }
    for (var Z = [], S = 0; S < r.length; S++) Z[S] = w(r[S], o);
    return Z;
  }
  function U(e, t) {
    if (!t) {
      var n = [];
      for (var r in e) {
        var o = q.exec(r),
          a = o && o[1];
        if (a && (!t || a === t)) {
          var i = e[r];
          if (i) for (var s = 0; s < i.length; s++) n.push(i[s]);
        }
      }
      return n;
    }
    var c = x[t];
    c || (W(t), (c = x[t]));
    var f = e[c[l]],
      h = e[c[u]];
    return f ? (h ? f.concat(h) : f.slice()) : h ? h.slice() : [];
  }
  function V(e, t) {
    var n = e.Event;
    n &&
      n.prototype &&
      t.patchMethod(n.prototype, "stopImmediatePropagation", function (e) {
        return function (t, n) {
          (t[G] = !0), e && e.apply(t, n);
        };
      });
  }
  function X(e, t, n, r, o) {
    var a = Zone.__symbol__(r);
    if (!t[a]) {
      var i = (t[a] = t[r]);
      (t[r] = function (a, s, c) {
        return (
          s &&
            s.prototype &&
            o.forEach(function (t) {
              var o = "".concat(n, ".").concat(r, "::") + t,
                a = s.prototype;
              try {
                if (a.hasOwnProperty(t)) {
                  var i = e.ObjectGetOwnPropertyDescriptor(a, t);
                  i && i.value
                    ? ((i.value = e.wrapWithCurrentZone(i.value, o)),
                      e._redefineProperty(s.prototype, t, i))
                    : a[t] && (a[t] = e.wrapWithCurrentZone(a[t], o));
                } else a[t] && (a[t] = e.wrapWithCurrentZone(a[t], o));
              } catch (e) {}
            }),
          i.call(t, a, s, c)
        );
      }),
        e.attachOriginToPatched(t[r], i);
    }
  }
  function Y(e, t, n) {
    if (!n || 0 === n.length) return t;
    var r = n.filter(function (t) {
      return t.target === e;
    });
    if (!r || 0 === r.length) return t;
    var o = r[0].ignoreProperties;
    return t.filter(function (e) {
      return -1 === o.indexOf(e);
    });
  }
  function J(e, t, n, r) {
    e && O(e, Y(e, t, n), r);
  }
  function K(e) {
    return Object.getOwnPropertyNames(e)
      .filter(function (e) {
        return e.startsWith("on") && e.length > 2;
      })
      .map(function (e) {
        return e.substring(2);
      });
  }
  function $(e, t) {
    if ((!b || w) && !Zone[e.symbol("patchEvents")]) {
      var r = t.__Zone_ignore_on_properties,
        o = [];
      if (E) {
        var a = window;
        o = o.concat([
          "Document",
          "SVGElement",
          "Element",
          "HTMLElement",
          "HTMLBodyElement",
          "HTMLMediaElement",
          "HTMLFrameSetElement",
          "HTMLFrameElement",
          "HTMLIFrameElement",
          "HTMLMarqueeElement",
          "Worker",
        ]);
        var i = (function e() {
          try {
            var e = _.navigator.userAgent;
            if (-1 !== e.indexOf("MSIE ") || -1 !== e.indexOf("Trident/"))
              return !0;
          } catch (e) {}
          return !1;
        })()
          ? [{ target: a, ignoreProperties: ["error"] }]
          : [];
        J(a, K(a), r ? r.concat(i) : r, n(a));
      }
      o = o.concat([
        "XMLHttpRequest",
        "XMLHttpRequestEventTarget",
        "IDBIndex",
        "IDBRequest",
        "IDBOpenDBRequest",
        "IDBDatabase",
        "IDBTransaction",
        "IDBCursor",
        "WebSocket",
      ]);
      for (var s = 0; s < o.length; s++) {
        var c = t[o[s]];
        c && c.prototype && J(c.prototype, K(c.prototype), r);
      }
    }
  }
  function Q(e, t) {
    t.patchMethod(e, "queueMicrotask", function (e) {
      return function (e, t) {
        Zone.current.scheduleMicroTask("queueMicrotask", t[0]);
      };
    });
  }
  Zone.__load_patch("util", function (n, s, c) {
    var p = K(n);
    (c.patchOnProperties = O),
      (c.patchMethod = j),
      (c.bindArguments = k),
      (c.patchMacroTask = z);
    var v = s.__symbol__("BLACK_LISTED_EVENTS"),
      d = s.__symbol__("UNPATCHED_EVENTS");
    n[d] && (n[v] = n[d]),
      n[v] && (s[v] = s[d] = n[v]),
      (c.patchEventPrototype = V),
      (c.patchEventTarget = B),
      (c.isIEOrEdge = N),
      (c.ObjectDefineProperty = t),
      (c.ObjectGetOwnPropertyDescriptor = e),
      (c.ObjectCreate = r),
      (c.ArraySlice = o),
      (c.patchClass = C),
      (c.wrapWithCurrentZone = h),
      (c.filterProperties = Y),
      (c.attachOriginToPatched = I),
      (c._redefineProperty = Object.defineProperty),
      (c.patchCallbacks = X),
      (c.getGlobalObjects = function () {
        return {
          globalSources: F,
          zoneSymbolEventNames: x,
          eventNames: p,
          isBrowser: E,
          isMix: w,
          isNode: b,
          TRUE_STR: u,
          FALSE_STR: l,
          ZONE_SYMBOL_PREFIX: f,
          ADD_EVENT_LISTENER_STR: a,
          REMOVE_EVENT_LISTENER_STR: i,
        };
      });
  });
  var ee = v("zoneTask");
  function te(e, t, n, r) {
    var o = null,
      a = null;
    n += r;
    var i = {};
    function s(t) {
      var n = t.data;
      return (
        (n.args[0] = function () {
          return t.invoke.apply(this, arguments);
        }),
        (n.handleId = o.apply(e, n.args)),
        t
      );
    }
    function c(t) {
      return a.call(e, t.data.handleId);
    }
    (o = j(e, (t += r), function (n) {
      return function (o, a) {
        if ("function" == typeof a[0]) {
          var u = {
              isPeriodic: "Interval" === r,
              delay: "Timeout" === r || "Interval" === r ? a[1] || 0 : void 0,
              args: a,
            },
            l = a[0];
          a[0] = function e() {
            try {
              return l.apply(this, arguments);
            } finally {
              u.isPeriodic ||
                ("number" == typeof u.handleId
                  ? delete i[u.handleId]
                  : u.handleId && (u.handleId[ee] = null));
            }
          };
          var f = p(t, a[0], u, s, c);
          if (!f) return f;
          var h = f.data.handleId;
          return (
            "number" == typeof h ? (i[h] = f) : h && (h[ee] = f),
            h &&
              h.ref &&
              h.unref &&
              "function" == typeof h.ref &&
              "function" == typeof h.unref &&
              ((f.ref = h.ref.bind(h)), (f.unref = h.unref.bind(h))),
            "number" == typeof h || h ? h : f
          );
        }
        return n.apply(e, a);
      };
    })),
      (a = j(e, n, function (t) {
        return function (n, r) {
          var o,
            a = r[0];
          "number" == typeof a ? (o = i[a]) : (o = a && a[ee]) || (o = a),
            o && "string" == typeof o.type
              ? "notScheduled" !== o.state &&
                ((o.cancelFn && o.data.isPeriodic) || 0 === o.runCount) &&
                ("number" == typeof a ? delete i[a] : a && (a[ee] = null),
                o.zone.cancelTask(o))
              : t.apply(e, r);
        };
      }));
  }
  function ne(e, t) {
    if (!Zone[t.symbol("patchEventTarget")]) {
      for (
        var n = t.getGlobalObjects(),
          r = n.eventNames,
          o = n.zoneSymbolEventNames,
          a = n.TRUE_STR,
          i = n.FALSE_STR,
          s = n.ZONE_SYMBOL_PREFIX,
          c = 0;
        c < r.length;
        c++
      ) {
        var u = r[c],
          l = s + (u + i),
          f = s + (u + a);
        (o[u] = {}), (o[u][i] = l), (o[u][a] = f);
      }
      var h = e.EventTarget;
      if (h && h.prototype)
        return t.patchEventTarget(e, t, [h && h.prototype]), !0;
    }
  }
  Zone.__load_patch("legacy", function (e) {
    var t = e[Zone.__symbol__("legacyPatch")];
    t && t();
  }),
    Zone.__load_patch("timers", function (e) {
      var t = "set",
        n = "clear";
      te(e, t, n, "Timeout"), te(e, t, n, "Interval"), te(e, t, n, "Immediate");
    }),
    Zone.__load_patch("requestAnimationFrame", function (e) {
      te(e, "request", "cancel", "AnimationFrame"),
        te(e, "mozRequest", "mozCancel", "AnimationFrame"),
        te(e, "webkitRequest", "webkitCancel", "AnimationFrame");
    }),
    Zone.__load_patch("blocking", function (e, t) {
      for (var n = ["alert", "prompt", "confirm"], r = 0; r < n.length; r++)
        j(e, n[r], function (n, r, o) {
          return function (r, a) {
            return t.current.run(n, e, a, o);
          };
        });
    }),
    Zone.__load_patch("EventTarget", function (e, t, n) {
      !(function r(e, t) {
        t.patchEventPrototype(e, t);
      })(e, n),
        ne(e, n);
      var o = e.XMLHttpRequestEventTarget;
      o && o.prototype && n.patchEventTarget(e, n, [o.prototype]);
    }),
    Zone.__load_patch("MutationObserver", function (e, t, n) {
      C("MutationObserver"), C("WebKitMutationObserver");
    }),
    Zone.__load_patch("IntersectionObserver", function (e, t, n) {
      C("IntersectionObserver");
    }),
    Zone.__load_patch("FileReader", function (e, t, n) {
      C("FileReader");
    }),
    Zone.__load_patch("on_property", function (e, t, n) {
      $(n, e);
    }),
    Zone.__load_patch("customElements", function (e, t, n) {
      !(function r(e, t) {
        var n = t.getGlobalObjects();
        (n.isBrowser || n.isMix) &&
          e.customElements &&
          "customElements" in e &&
          t.patchCallbacks(t, e.customElements, "customElements", "define", [
            "connectedCallback",
            "disconnectedCallback",
            "adoptedCallback",
            "attributeChangedCallback",
          ]);
      })(e, n);
    }),
    Zone.__load_patch("XHR", function (e, t) {
      !(function n(e) {
        var n = e.XMLHttpRequest;
        if (n) {
          var f = n.prototype,
            h = f[s],
            d = f[c];
          if (!h) {
            var _ = e.XMLHttpRequestEventTarget;
            if (_) {
              var g = _.prototype;
              (h = g[s]), (d = g[c]);
            }
          }
          var y = "readystatechange",
            k = "scheduled",
            m = j(f, "open", function () {
              return function (e, t) {
                return (e[o] = 0 == t[2]), (e[u] = t[1]), m.apply(e, t);
              };
            }),
            T = v("fetchTaskAborting"),
            b = v("fetchTaskScheduling"),
            E = j(f, "send", function () {
              return function (e, n) {
                if (!0 === t.current[b]) return E.apply(e, n);
                if (e[o]) return E.apply(e, n);
                var r = {
                    target: e,
                    url: e[u],
                    isPeriodic: !1,
                    args: n,
                    aborted: !1,
                  },
                  a = p("XMLHttpRequest.send", S, r, Z, P);
                e && !0 === e[l] && !r.aborted && a.state === k && a.invoke();
              };
            }),
            w = j(f, "abort", function () {
              return function (e, n) {
                var o = (function a(e) {
                  return e[r];
                })(e);
                if (o && "string" == typeof o.type) {
                  if (null == o.cancelFn || (o.data && o.data.aborted)) return;
                  o.zone.cancelTask(o);
                } else if (!0 === t.current[T]) return w.apply(e, n);
              };
            });
        }
        function Z(e) {
          var n = e.data,
            o = n.target;
          (o[i] = !1), (o[l] = !1);
          var u = o[a];
          h || ((h = o[s]), (d = o[c])), u && d.call(o, y, u);
          var f = (o[a] = function () {
            if (o.readyState === o.DONE)
              if (!n.aborted && o[i] && e.state === k) {
                var r = o[t.__symbol__("loadfalse")];
                if (0 !== o.status && r && r.length > 0) {
                  var a = e.invoke;
                  (e.invoke = function () {
                    for (
                      var r = o[t.__symbol__("loadfalse")], i = 0;
                      i < r.length;
                      i++
                    )
                      r[i] === e && r.splice(i, 1);
                    n.aborted || e.state !== k || a.call(e);
                  }),
                    r.push(e);
                } else e.invoke();
              } else n.aborted || !1 !== o[i] || (o[l] = !0);
          });
          return (
            h.call(o, y, f),
            o[r] || (o[r] = e),
            E.apply(o, n.args),
            (o[i] = !0),
            e
          );
        }
        function S() {}
        function P(e) {
          var t = e.data;
          return (t.aborted = !0), w.apply(t.target, t.args);
        }
      })(e);
      var r = v("xhrTask"),
        o = v("xhrSync"),
        a = v("xhrListener"),
        i = v("xhrScheduled"),
        u = v("xhrURL"),
        l = v("xhrErrorBeforeScheduled");
    }),
    Zone.__load_patch("geolocation", function (t) {
      t.navigator &&
        t.navigator.geolocation &&
        (function n(t, r) {
          for (
            var o = t.constructor.name,
              a = function (n) {
                var a = r[n],
                  i = t[a];
                if (i) {
                  if (!m(e(t, a))) return "continue";
                  t[a] = (function (e) {
                    var t = function () {
                      return e.apply(this, k(arguments, o + "." + a));
                    };
                    return I(t, e), t;
                  })(i);
                }
              },
              i = 0;
            i < r.length;
            i++
          )
            a(i);
        })(t.navigator.geolocation, ["getCurrentPosition", "watchPosition"]);
    }),
    Zone.__load_patch("PromiseRejectionEvent", function (e, t) {
      function n(t) {
        return function (n) {
          U(e, t).forEach(function (r) {
            var o = e.PromiseRejectionEvent;
            if (o) {
              var a = new o(t, { promise: n.promise, reason: n.rejection });
              r.invoke(a);
            }
          });
        };
      }
      e.PromiseRejectionEvent &&
        ((t[v("unhandledPromiseRejectionHandler")] = n("unhandledrejection")),
        (t[v("rejectionHandledHandler")] = n("rejectionhandled")));
    }),
    Zone.__load_patch("queueMicrotask", function (e, t, n) {
      Q(e, n);
    });
});
