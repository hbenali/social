<template>
  <v-app id="brandingTopBar" class="border-box-sizing" flat>
    <v-container pa-0 pl-3>
      <div class="d-flex mx-0 pa-0">
        <a :href="homeLink" class=" pr-3 logoContainer">
          <img src="/portal/rest/v1/platform/branding/logo" alt="Logo" />
        </a>
        <a v-show="brandingCompanyName" :href="homeLink" class=" pl-2 align-self-center brandingContainer">
          <span class="subtitle-2 font-weight-bold">{{ brandingCompanyName }}</span>
        </a>
      </div>
    </v-container>
  </v-app>
</template>
<script>
export default {
  data() {
    return {
      homeLink: `${eXo.env.portal.context}/${eXo.env.portal.portalName}`,
      branding: null,
    };
  },
  computed: {
    brandingCompanyName() {
      return this.branding && this.branding.companyName;
    }
  },
  created() {
    return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/platform/branding`, {
      method: 'GET',
      credentials: 'include',
    })
      .then(resp => resp.json())
      .then(data => this.branding = data);
  }
};
</script>